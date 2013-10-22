package org.apache.solr.listener;

import java.util.Calendar;
import java.util.Date;
import java.util.Properties;
import java.util.Timer;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;

import org.apache.solr.handler.dataimport.scheduler.ApplicationListener;
import org.apache.solr.handler.dataimport.scheduler.DeltaImportHTTPPostScheduler;
import org.apache.solr.handler.dataimport.scheduler.FullImportHTTPPostScheduler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SolrListener extends ApplicationListener {

	private static final Logger logger = LoggerFactory
			.getLogger(SolrListener.class);

	@Override
	public void contextInitialized(ServletContextEvent servletContextEvent) {
		ServletContext servletContext = servletContextEvent.getServletContext();

		// 设置solr.solr.home
		try {
			Properties p = new Properties();
			p.load(servletContext.getResourceAsStream("WEB-INF/conf/config.properties"));
			String sys_runtime = p.getProperty("sys.runtime");
			String sys_ha = p.getProperty("sys.ha");
			
			if(sys_runtime.equals("dev") || sys_runtime.equals("test")){
				//开发环境与测试环境，不分都为solr主服务器
				System.setProperty("solr.solr.home", servletContext.getRealPath("WEB-INF/conf/"+ sys_runtime +"/master/multicore"));				
			}else if(sys_runtime.equals("www")){
				//生产环境，区分都为solr主/从服务器
				if(sys_ha.equals("master") || sys_ha.equals("slave")){
					System.setProperty("solr.solr.home", servletContext.getRealPath("WEB-INF/conf/"+ sys_runtime +"/"+ sys_ha +"/multicore"));
				}else{
					logger.error("The config.properties file sys.ha configuration error.");
				}
			}else{
				logger.error("The config.properties file sys.runtime configuration error.");				
			}
			
		} catch (Exception e) {
			logger.error("Set solr.sor.home error:", e);
		}
		

		// ApplicationListener中的创建索引监听
		try {			
			// 增量更新任务计划
			// create the timer and timer task objects
			Timer timer = new Timer();
			DeltaImportHTTPPostScheduler task = new DeltaImportHTTPPostScheduler(
					servletContext.getServletContextName(), timer);

			// get our interval from HTTPPostScheduler
			int interval = task.getIntervalInt();

			// get a calendar to set the start time (first run)
			Calendar calendar = Calendar.getInstance();

			// set the first run to now + interval (to avoid fireing while the
			// app/server is starting)
			calendar.add(Calendar.MINUTE, interval);
			Date startTime = calendar.getTime();

			// schedule the task
			timer.scheduleAtFixedRate(task, startTime, 1000 * 60 * interval);

			// save the timer in context
			servletContext.setAttribute("timer", timer);

			// 重做索引任务计划
			Timer fullImportTimer = new Timer();
			FullImportHTTPPostScheduler fullImportTask = new FullImportHTTPPostScheduler(
					servletContext.getServletContextName(), fullImportTimer);

			int reBuildIndexInterval = fullImportTask
					.getReBuildIndexIntervalInt();
			if (reBuildIndexInterval <= 0) {
				logger.warn("Full Import Schedule disabled");
				return;
			}

			Calendar fullImportCalendar = Calendar.getInstance();
			Date beginDate = fullImportTask.getReBuildIndexBeginTime();
			fullImportCalendar.setTime(beginDate);
			
			/**
			 * Linpn: 优化了个BUG(系统启动要么不执行重建索引，要么重复执行N次)。
			 * 现在可以在系统启动时能正常执行重建索引操作，且只执行一次。 
			 */
			while (((System.currentTimeMillis() - fullImportCalendar
					.getTimeInMillis()) / 1000 / 60) > reBuildIndexInterval) {
				fullImportCalendar.add(Calendar.MINUTE, reBuildIndexInterval);
			}
			
			Date fullImportStartTime = fullImportCalendar.getTime();

			// schedule the task
			fullImportTimer.scheduleAtFixedRate(fullImportTask,
					fullImportStartTime, 1000 * 60 * reBuildIndexInterval);

			// save the timer in context
			servletContext.setAttribute("fullImportTimer", fullImportTimer);

		} catch (Exception e) {
			if (e.getMessage().endsWith("disabled")) {
				logger.warn("Schedule disabled");
			} else {
				logger.error("Problem initializing the scheduled task: ", e);
			}
		}
	}

	@Override
	public void contextDestroyed(ServletContextEvent servletContextEvent) {
		super.contextDestroyed(servletContextEvent);
	}
}
