package com.baixc.shopmng.web.product;

import org.springframework.web.servlet.ModelAndView;

import com.baixc.framework.web.HttpServletExtendRequest;
import com.baixc.framework.web.HttpServletExtendResponse;
import com.baixc.ucenter.web.controller.PageAdmController;

/**
 * 我要卖、编辑商品 页面
 * @author Linpn
 */
public class ProductPublishAction extends PageAdmController {

//	@Resource
//	private ProductService productService;
//	@Resource
//	private BrandsService brandsService;
//	@Resource
//	protected CategoryService categoryService;
//	@Resource
//	private PropertiesService propertiesService;
	
	
	@Override
	public void execute(HttpServletExtendRequest request,
			HttpServletExtendResponse response, ModelAndView modeview) {

//		//判断是添加不是编辑
//		String action = request.getParameter("action");
//		if(action!=null && action.equals("edit")){
//			String productId = request.getParameter("productId");
//			if(productId==null)
//				return;
//			
//			Product product = productService.getProduct(Long.valueOf(productId), ProductQueryMode.ALL);			
//			modeview.addObject("product", product);
//			modeview.addObject("module_title", "编辑商品");
//		}
//		
//		
//		//商品类目树和叶子列表
//		String treeCategory = categoryService.getTreeCategoryOfJSON().toString();
//		modeview.addObject("treeCategory", treeCategory);
//		
//		//加载商品品牌列表
//		List<Brands> cbbBrands = brandsService.getBrandsList();
//		modeview.addObject("cbbBrands", JSONArray.fromObject(cbbBrands));
		
	}
		
	
//	/**
//	 * Ajax请求：保存商品
//	 * @param request
//	 * @param response
//	 * @throws IOException 
//	 */
//	public void saveProduct(HttpServletExtendRequest request, 
//			HttpServletExtendResponse response) throws IOException {
//		PrintWriter out = response.getWriter();
//		
//		try{
//			//获取参数
//			String productId = request.getParameter("productId");			//商品ID
//			String cateId = request.getParameter("cateId");				//商品所属类别
//			String brandId = request.getParameter("brandId");			//品牌
//			String typeId = request.getParameter("typeId");				//店铺自定义分类
//			String productName = request.getParameter("productName");		//商品的名称
//			String productImage = request.getParameter("productImage");		//商品图片URL
//			String productNumber = request.getParameter("productNumber");	//商品库存数量
//			String weight = request.getParameter("weight");				//商品的重量
//			String marketPrice = request.getParameter("marketPrice");	//市场售价
//			String shopPrice = request.getParameter("shopPrice");		//本店售价
//			String productBrief = request.getParameter("productBrief");		//商品的简短描述
//			String productDesc = request.getParameter("productDesc");		//商品的详细描述
//			String sellerNote = request.getParameter("sellerNote");		//商家备注
//			String isReal = request.getParameter("isReal");				//是否是实物
//			String isOnSale = request.getParameter("isOnSale");			//是否上架销售
//			String hasInvoice = request.getParameter("hasInvoice");		//是否有发票
//			String hasWarranty = request.getParameter("hasWarranty");	//是否有保修
//			
//			//创建对象
//			Product product = new Product();
//			product.setCateId(Integer.valueOf(cateId));
//			product.setBrandId(Integer.valueOf(brandId));
//			product.setTypeId(Integer.valueOf(typeId));
//			product.setProductName(productName);
//			product.setProductImage(productImage);
//			product.setProductNumber(Integer.valueOf(productNumber));
//			product.setWeight(new BigDecimal(weight));
//			product.setMarketPrice(new BigDecimal(marketPrice));
//			product.setShopPrice(new BigDecimal(shopPrice));
//			product.setProductBrief(productBrief);
//			product.setProductDesc(productDesc);
//			product.setSellerNote(sellerNote);
//			product.setIsReal(Byte.valueOf(isReal));
//			product.setIsOnSale(Byte.valueOf(isOnSale));
//			product.setHasInvoice(Byte.valueOf(hasInvoice));
//			product.setHasWarranty(Byte.valueOf(hasWarranty));
//			product.setPublishTime(product.getIsOnSale().byteValue()==1?new Date():null);
//			
//			if(productId==null || productId.equals("")){
//				//添加对象
//				product.setProductSn(UUID.randomUUID().toString().toUpperCase());
//				product.setCreateTime(new Date());
//				productService.addProduct(product);
//			}else{
//				//编辑对象
//				product.setProductId(Long.valueOf(productId));
//				product.setUpdateTime(new Date());
//				productService.editProduct(product);
//			}
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
//	 * Ajax请求：获取商品属性
//	 * @param request
//	 * @param response
//	 * @throws IOException 
//	 */
//	public void getProductProps(HttpServletExtendRequest request, 
//			HttpServletExtendResponse response) throws IOException {
//		PrintWriter out = response.getWriter();
//		
//		try{
//			String cateId = request.getParameter("cateId");
//			String productId = request.getParameter("productId");	
//						
//			List<Properties> propsList = propertiesService.getPropertiesList(Integer.valueOf(cateId));
//			List<ProductProps> productProps = new ArrayList<ProductProps>();
//			
//			if(productId!=null && !productId.equals("")){
//				Product product = productService.getProduct(Long.valueOf(productId),ProductQueryMode.WITH_PROPS);
//				if(product!=null)
//					productProps = product.getProperties();
//			}			
//		
//			//输出数据
//			JSONObject json = new JSONObject();
//			json.put("success", true);
//			
//			JSONObject data = new JSONObject();			
//			for(Properties prop : propsList){
//				String value = "";
//				for(ProductProps productProp : productProps){
//					if(prop.getPropsId().equals(productProp.getPropsId()))
//						value = productProp.getPropsValue();
//				}
//				data.put(prop.getPropsName(), value);
//			}			
//			json.put("data", data);
//						
//			out.println(json.toString());			
//	
//		}catch(Exception e){
//			out.println("{success: false, msg: '"+ e.getMessage() +"'}");
//			e.printStackTrace();
//		}
//		
//		out.close();
//	}
	
	

}
