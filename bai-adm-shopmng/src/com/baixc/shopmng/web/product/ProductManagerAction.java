package com.baixc.shopmng.web.product;

import org.springframework.web.servlet.ModelAndView;

import com.baixc.framework.web.HttpServletExtendRequest;
import com.baixc.framework.web.HttpServletExtendResponse;
import com.baixc.ucenter.web.controller.PageAdmController;

/**
 * 出售中的商品页面
 * @author Linpn
 */
public class ProductManagerAction extends PageAdmController {

//	@Resource
//	private ProductService productService;
	
	
	@Override
	public void execute(HttpServletExtendRequest request,
			HttpServletExtendResponse response, ModelAndView modeview) {
		
	}
	
	
//	/**
//	 * Ajax请求：获取出售中的商品列表
//	 * @param request
//	 * @param response
//	 * @throws IOException 
//	 */
//	public void getProductList(HttpServletExtendRequest request, 
//			HttpServletExtendResponse response) throws IOException {
//		PrintWriter out = response.getWriter();
//		
//		try{
//			//获取参数
//			String callback=request.getParameter("callback");
//			String sort = request.getParameter("sort");
//			String dir = request.getParameter("dir");
//			long start = request.getParameter("start")==null?0:Integer.valueOf(request.getParameter("start"));
//			long limit = request.getParameter("limit")==null?0:Integer.valueOf(request.getParameter("limit"));
//			String isOnSale = request.getParameter("isOnSale");
//			String isDelete = request.getParameter("isDelete");
//			String searchKey = request.getParameter("searchKey", "UTF-8");
//			
//			//查询参数
//			Map<String,Object> params = new HashMap<String,Object>();
//			params.put("isOnSale", isOnSale);
//			params.put("isDelete", isDelete);
//			params.put("searchKey", searchKey);
//			
//			//排序数据
//			Map<String,String> sorters = new HashMap<String,String>();
//			sorters.put(sort, dir);
//			
//			//查询数据
//			List<Product> list = productService.getProductList(params, sorters, start, limit, ProductQueryMode.SIMPLE);
//			long count = productService.getProductCount(params);
//			
//			//组装JSON
//			JSONObject json = new JSONObject();
//			json.put("count", count);
//			json.put("records", new JSONArray());
//			
//			JSONArray records = json.getJSONArray("records");
//			for(Product good : list) {
//				JSONObject record = JSONObject.fromObject(good, JsonConfigGlobal.jsonConfig);							
//				record.put("publishTime", DateUtils.format(good.getPublishTime(), "yyyy-MM-dd\nHH:mm").replace("\n", "<br/>"));
//				
//				records.add(record);
//			}
//			
//			//输出数据
//			out.println(callback + "("+ json +")");
//			
//		}catch(Exception e){
//			out.println("{success: false, msg: '"+ e.getMessage() +"'}");
//			e.printStackTrace();
//		}
//		
//		out.close();
//	}
//	
//	
//	/**
//	 * Ajax请求：恢复商品
//	 * @param request
//	 * @param response
//	 * @throws IOException 
//	 */
//	public void recoveryProduct(HttpServletExtendRequest request, 
//			HttpServletExtendResponse response) throws IOException {
//		PrintWriter out = response.getWriter();
//		
//		try{
//			//获取参数
//			String productIds = request.getParameter("productIds");	
//			long[] _productIds = ConvertUtils.toArrLong(productIds.split(","));	
//			
//			productService.recoveryProduct(_productIds);
//			
//			//输出数据
//			out.println("{success: true}");
//
//		}catch(Exception e){
//			out.println("{success: false, msg: '"+ e.getMessage() +"'}");
//			e.printStackTrace();
//		}
//		
//		out.close();
//	}
//	
//	
//	/**
//	 * Ajax请求：删除商品
//	 * @param request
//	 * @param response
//	 * @throws IOException 
//	 */
//	public void delProduct(HttpServletExtendRequest request, 
//			HttpServletExtendResponse response) throws IOException {
//		PrintWriter out = response.getWriter();
//		
//		try{
//			//获取参数
//			String productIds = request.getParameter("productIds");	
//			long[] _productIds = ConvertUtils.toArrLong(productIds.split(","));
//			
//			productService.delProduct(_productIds);
//			
//			//输出数据
//			out.println("{success: true}");
//			
//		}catch(Exception e){
//			out.println("{success: false, msg: '"+ e.getMessage() +"'}");
//			e.printStackTrace();
//		}
//		
//		out.close();
//	}
//	
//	
//	/**
//	 * Ajax请求：彻底删除商品
//	 * @param request
//	 * @param response
//	 * @throws IOException 
//	 */
//	public void delRealProduct(HttpServletExtendRequest request, 
//			HttpServletExtendResponse response) throws IOException {
//		PrintWriter out = response.getWriter();
//		
//		try{
//			//获取参数
//			String productIds = request.getParameter("productIds");	
//			long[] _productIds = ConvertUtils.toArrLong(productIds.split(","));
//			
//			productService.delRealProduct(_productIds);
//			
//			//输出数据
//			out.println("{success: true}");
//
//		}catch(Exception e){
//			out.println("{success: false, msg: '"+ e.getMessage() +"'}");
//			e.printStackTrace();
//		}
//		
//		out.close();
//	}
//	
//	
//	/**
//	 * Ajax请求：清空已删除的商品
//	 * @param request
//	 * @param response
//	 * @throws IOException 
//	 */
//	public void clearDeledProduct(HttpServletExtendRequest request, 
//			HttpServletExtendResponse response) throws IOException {
//		PrintWriter out = response.getWriter();
//		
//		try{
//			//获取对象
//			Map<String,Object> params = new HashMap<String,Object>();
//			params.put("isDelete", 1);			
//			List<Product> list = productService.getProductList(params, null, null, null, ProductQueryMode.SIMPLE);
//			
//			long[] productIds = new long[list.size()];
//			for(int i=0;i<productIds.length;i++){
//				Product product = (Product)list.get(i);
//				productIds[i] = product.getProductId();
//			}
//			
//			productService.delRealProduct(productIds);
//			
//			//输出数据
//			out.println("{success: true}");
//
//		}catch(Exception e){
//			out.println("{success: false, msg: '"+ e.getMessage() +"'}");
//			e.printStackTrace();
//		}
//		
//		out.close();
//	}
//	
//		
//	/**
//	 * Ajax请求：上架商品
//	 * @param request
//	 * @param response
//	 * @throws IOException 
//	 */
//	public void upShelfProduct(HttpServletExtendRequest request, 
//			HttpServletExtendResponse response) throws IOException {
//		PrintWriter out = response.getWriter();
//		
//		try{
//			//获取参数
//			String productIds = request.getParameter("productIds");	
//			long[] _productIds = ConvertUtils.toArrLong(productIds.split(","));
//			
//			productService.upShelfProduct(_productIds);
//			
//			//输出数据
//			out.println("{success: true}");
//			
//		}catch(Exception e){
//			out.println("{success: false, msg: '"+ e.getMessage() +"'}");
//			e.printStackTrace();
//		}
//		
//		out.close();
//	}
//	
//	
//	/**
//	 * Ajax请求：下架商品
//	 * @param request
//	 * @param response
//	 * @throws IOException 
//	 */
//	public void offShelfProduct(HttpServletExtendRequest request, 
//			HttpServletExtendResponse response) throws IOException {
//		PrintWriter out = response.getWriter();
//		
//		try{
//			//获取参数
//			String productIds = request.getParameter("productIds");	
//			long[] _productIds = ConvertUtils.toArrLong(productIds.split(","));
//			
//			productService.offShelfProduct(_productIds);
//			
//			//输出数据
//			out.println("{success: true}");
//			
//		}catch(Exception e){
//			out.println("{success: false, msg: '"+ e.getMessage() +"'}");
//			e.printStackTrace();
//		}
//		
//		out.close();
//	}
//	
//	
//	
//	/**
//	 * Ajax请求：设置价格
//	 * @param request
//	 * @param response
//	 * @throws IOException 
//	 */
//	public void modifyPrice(HttpServletExtendRequest request, 
//			HttpServletExtendResponse response) throws IOException {
//		PrintWriter out = response.getWriter();
//		
//		try{
//			//获取参数
//			String productId = request.getParameter("productId");
//			String shopPrice = request.getParameter("shopPrice");
//			
//			productService.modifyPrice(Long.valueOf(productId), new BigDecimal(shopPrice));
//			
//			//输出数据
//			out.println("{success: true}");
//			
//		}catch(Exception e){
//			out.println("{success: false, msg: '"+ e.getMessage() +"'}");
//			e.printStackTrace();
//		}
//		
//		out.close();
//	}
//	
//	
//	
//	/**
//	 * Ajax请求：设置价格
//	 * @param request
//	 * @param response
//	 * @throws IOException 
//	 */
//	public void modifyInventory(HttpServletExtendRequest request, 
//			HttpServletExtendResponse response) throws IOException {
//		PrintWriter out = response.getWriter();
//		
//		try{
//			//获取参数
//			String productId = request.getParameter("productId");
//			String productNumber = request.getParameter("productNumber");
//			
//			productService.modifyInventory(Long.valueOf(productId), Integer.valueOf(productNumber));
//			
//			//输出数据
//			out.println("{success: true}");
//			
//		}catch(Exception e){
//			out.println("{success: false, msg: '"+ e.getMessage() +"'}");
//			e.printStackTrace();
//		}
//		
//		out.close();
//	}
	


}
