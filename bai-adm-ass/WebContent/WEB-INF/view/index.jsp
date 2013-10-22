<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<title>后台管理系统</title>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<meta http-equiv="X-UA-Compatible" content="chrome=1">
<meta name="fragment" content="!">
<script type="text/javascript">if(window.parent && window.parent!=window)window.parent.location.href=window.location.href;</script>
<link rel="shortcut icon" type="image/ico" href="favicon.ico" />
<link rel="stylesheet" href="WEB-RES/script/extjs4.2/app/resources/css/app.css" type="text/css" />
<script type="text/javascript" src="WEB-RES/script/extjs4.2/app/ext-all.js"></script>
<script type="text/javascript" src="WEB-RES/script/extjs4.2/app/data.js"></script>
<script type="text/javascript">
Docs.data.casass = Ext.decode('${casass}');
Docs.data.shopmng = Ext.decode('${shopmng}');
Docs.data.erpcrm = Ext.decode('${erpcrm}');
Docs.data.search = Ext.decode('${search}');
</script>
<script type="text/javascript" src="WEB-RES/script/extjs4.2/app/app.js"></script>
</head>
<body id="ext-body">
  <div id="loading"><span class="title"></span><span class="logo"></span></div>
  <div id="header-content"><strong>Ext JS 4.2.0 </strong>Sencha Docs</div>
  <div id='welcome-content' style='display:none'><div id="extjs-welcome">
    <div class="content">
        <header>
            <h5 class="logo"><a href="http://www.sencha.com">Sencha</a></h5>
        </header>
        <section>
            <div class="auto_columns two">
                <div class="column">
                	<a href="logout">退出系统</a>
                    <h2>Welcome to <strong>Ext JS 4.2</strong>!</h2>
                    <p class="intro">Ext JS 4.2 is a pure JavaScript application framework that works on all modern browsers from IE6 to the latest version of Chrome. It enables you to create the best cross-platform applications using nothing but a browser, and has a phenomenal API.</p>
                    <p class="intro">This is the biggest upgrade we've ever made to Ext JS and we think you're going to love it.</p>
                    <p class="button-group">
                        <a href="#!/example" class="button-link">View the Examples</a>
                        <a href="http://www.sencha.com/forum/forumdisplay.php?79-Ext-JS-Community-Forums-4.x" class="more-icon">Discuss Ext JS 4.2 on the forum</a>
                  </p>
                </div>
                <div class="column">
                    <img src="WEB-RES/script/extjs4.2/app/resources/images/hero-extjs4-alt.png" class="feature-img pngfix" />
                    <div class="auto_columns two right">
                        <div class="column">
                            <h3>What&rsquo;s New</h3>
                            <p>We have also been posting summaries of new features and changes to <a href="http://www.sencha.com/blog/">our blog</a>:</p>
                            <ul class="type13">
                                <li><a href="#!/guide/drawing_and_charting">Drawing &amp; Charting</a></li>
                                <li><a href="http://www.sencha.com/blog/ext-js-4-anatomy-of-a-model/">Anatomy of a Model</a></li>
                                <li><a href="#!/guide/data">Data Package</a></li>
                                <li><a href="http://www.sencha.com/blog/countdown-to-ext-js-4-dynamic-loading-and-new-class-system/">Dynamic Loading and the New Class System</a></li>
                            </ul>
                            <p><a href="http://www.sencha.com/products/extjs/" class="more-icon">Learn more on sencha.com</a></p>
                            <p><a href="#!/api" class="more-icon">API Docs</a></p>
                        </div>
                        <div class="column">
                            <h3>Upgrading</h3>
                            <p>First, check out our <a href="#!/guide/upgrade">overview guide</a> to see what has changed.</p>

                            <p>Sencha also offers <a href="http://www.sencha.com/training/">training courses</a> and <a href="http://www.sencha.com/support/services/">professional services</a> for companies wishing to use Ext JS 4.2.</p>
                        </div>
                    </div>

                </div>
            </div>
        </section>
    </div>
    <div class="news">
        <div class="l">
            <h1>SDK Updates</h1>
            <div class="item"><span class="date">Mar 13 2013</span> Ext 4.2.0 Released</div>
            <div class="item"><span class="date">Oct 26 2012</span> Ext 4.1.3 Released (support subscribers only)</div>
            <div class="item"><span class="date">Sep 8 2012</span> Ext 4.1.2 Released (support subscribers only)</div>
            <div class="item"><span class="date">Jul 5 2012</span> Ext 4.1.1 Released</div>
            <div class="item"><span class="date">Apr 24 2012</span> Ext 4.1.0 Released</div>
            <div class="item"><span class="date">Oct 19 2011</span> Ext 4.0.7 Released</div>
            <div class="item"><span class="date">Aug 30 2011</span> Ext 4.0.6 Released (support subscribers only)</div>
            <div class="item"><span class="date">Jul 24 2011</span> Ext 4.0.5 Released (support subscribers only)</div>
            <div class="item"><span class="date">Jun 29 2011</span> Ext 4.0.4 Released (support subscribers only)</div>
            <div class="item"><span class="date">Jun 14 2011</span> Ext 4.0.3 Released (support subscribers only)</div>
            <div class="item"><span class="date">Jun 9 2011</span> Ext 4.0.2a Released</div>
            <div class="item"><span class="date">May 17 2011</span> Ext 4.0.1 Released</div>
            <div class="item"><span class="date">Apr 26 2011</span> Ext 4.0.0 Final Released</div>
        </div>
        <div class="r">
        </div>
    </div>
    </div>
  </div>  
</body>
</html>