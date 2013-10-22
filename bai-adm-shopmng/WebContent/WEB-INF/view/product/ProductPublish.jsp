<?xml version="1.0" encoding="UTF-8" ?>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<%@ include file="../meta.jsp" %>
<title>${module_title}</title>
<script type="text/javascript">
Ext.onReady(function(){
    Ext.tip.QuickTipManager.init();    
    
    //业务请求的URL
    Url = {
    	saveProduct: '${page_context}/product/ProductPublish.jspx?func=saveProduct',
    	getProductProps: '${page_context}/product/ProductPublish.jspx?func=getProductProps'
    };
    
    
	//部门tree与combobox的数据
	var treeCategory = Ext.decode('${treeCategory}');
    
 	
 	//添加和编辑窗口
 	var frmProduct = Ext.create('Ext.form.Panel', {
    	id: 'frmProduct',
      	region: 'center',
        bodyStyle:'padding:15px',
        border: false,
       	autoScroll: true,
        fieldDefaults: {
            labelAlign: 'left',
            msgTarget: 'side'
        },
        items: [{
            xtype: 'container',
            defaults: {
                margin: '10 0 10 0'
            },
            items: [{
                xtype: 'container',
                layout: {
                    type: 'hbox'
                },
                items: [{
                    xtype:'textfield',
                    id: 'txtProductName',
                    name: 'productName',
                    fieldLabel: '商品标题',
                    width: 600,
                    allowBlank: false,
                    value: '${product.productName}'
                },{
                    xtype: 'label',
                    text: '还能输入25字',
                    margin: '2 0 0 5'
                }]
            },{
                xtype: 'container',
                layout: {
                    type: 'hbox'
                },
                items: [{
                    xtype:'combobox',
                    id: 'cbbCateId',
                    name: 'cateId',
                    fieldLabel: '类目',
                    width: 500,
			        editable: false,
			        tpl: "<tpl for='treeCategory'><div id='treeCategory' style='height:200px;'></div></tpl>",
			      	emptyText: '请选择类目...',  
                   	store: treeCategory.cbbCategory,
					value: '${product.cateId}'==''?0:Number('${product.cateId}'),
					tree: Ext.create('Ext.tree.Panel', {
						width: '100%',
						border:false, 
					    rootVisible: false,  
						store: Ext.create('Ext.data.TreeStore', {
						    root: treeCategory
						}),
						listeners: {
							itemclick: function(obj, record, item, index, e, options){
								if(record.data.leaf){
									//选择列表
									var cbbCate = Ext.getCmp("cbbCateId"); 									
									try{
										cbbCate.collapse();									
										cbbCate.select(Number(record.data.id));
									}catch(err){}									

									//加载类目属性
									Ext.getCmp("productProps").getLoader().load({
					                    params: {
					                    	cateId: cbbCate.getValue(),
					                    	productId: '${product.productId}'
				                    	}
									});
								}
							}
						}
					}),
			        listeners : {
						expand: function(field, options){
							Ext.getCmp("cbbCateId").tree.render('treeCategory');
							Ext.get("treeCategory").parent().parent().addCls("x-boundlist");
						}
					}
                },{
                    xtype: 'label',
                    html: '<a href="#" target="_blank">编辑类目</a>',
                    margin: '2 0 0 5'
                }]
            },{
                xtype: 'container',
                layout: {
                    type: 'hbox'
                },
                items: [{
                    xtype:'combobox',
                    id: 'cbbBrandId',
                    name: 'brandId',
                    fieldLabel: '品牌',
                    width: 500,
			        editable: false,
			      	emptyText: '请选择品牌...', 
                    valueField: 'brandId',
        	      	displayField: 'brandName',
                    store:  Ext.create('Ext.data.Store', {
                        fields: ['brandId', 'brandName'],
                        data : Ext.decode('${cbbBrands}')
                    }),                    
                    value: '${product.brandId}'==''?0:Number('${product.brandId}')
                },{
                    xtype: 'label',
                    html: '<a href="#" target="_blank">编辑品牌</a>',
                    margin: '2 0 0 5'
                }]
            },{
                xtype: 'container',
                layout: {
                    type: 'hbox'
                },
                items: [{
                    xtype:'numberfield',
                    fieldLabel: '商品重量',
                    name: 'weight',
                    minValue: 0,
                    value: '${product.weight}'
                },{
                    xtype: 'label',
                    text: '千克',
                    margin: '2 0 0 5'
                }]
            },{
                xtype: 'container',
                layout: {
                    type: 'hbox'
                },
                items: [{
                    xtype:'numberfield',
                    fieldLabel: '商品数量',
                    name: 'productNumber',
                    minValue: 0,
                    value: '${product.productNumber}'
                },{
                    xtype: 'label',
                    text: '件',
                    margin: '2 0 0 5'
                }]
            },{
                xtype: 'container',
                layout: {
                    type: 'hbox'
                },
                items: [{
                    xtype:'numberfield',
                    fieldLabel: '市场售价',
                    name: 'marketPrice',
                    minValue: 0,
                    value: '${product.marketPrice}'
                },{
                    xtype: 'label',
                    text: '元',
                    margin: '2 0 0 5'
                }]
            },{
                xtype: 'container',
                layout: {
                    type: 'hbox'
                },
                items: [{
                    xtype:'numberfield',
                    fieldLabel: '本店售价',
                    name: 'shopPrice',
                    minValue: 0,
                    value: '${product.shopPrice}'
                },{
                    xtype: 'label',
                    text: '元',
                    margin: '2 0 0 5'
                }]
            }]
        },{
            xtype: 'container',
            layout: 'column',
            items: [{
                xtype: 'label',
                text: '商品属性:',
                margin: '5 0 10 0'
            }, Ext.create('Ext.grid.property.Grid', {
            	id: 'productProps',
                width: 550,
                height: 200,
                margin: '5 0 10 48',
                source: {},
                loader: {
                    url: Url.getProductProps,
                    renderer: function(loader, response, active) {
                        var json = Ext.decode(response.responseText);
                        if(json.success){
	                        loader.getTarget().setSource(json.data);
	                        return true;
                        }else{
                        	Ext.Msg.alert('提示', json.msg);
	                        return false;
                        }
                    }
                }
            })]
        },{
            xtype: 'container',
            layout: 'column',
            items: [{
                xtype: 'label',
                text: '商品图片:',
                margin: '10 0 10 0'
            }, {
			    xtype:'panel',
			    width: 550,
			    height: 120,
                margin: '10 0 10 48',
			    html: '${product.productImage}'
			}]
        },{
            xtype: 'container',
            layout: 'column',
            defaults: {
                margin: '10 0 10 0'
            },
            items: [{
			    xtype:'textfield',
			    name: 'typeId',
			    fieldLabel: '店铺中所属分类',
			    value: '${product.typeId}'==''?0:Number('${product.typeId}'),
			    width: 855
			},{
                xtype:'textarea',
                fieldLabel: '商品的摘要描述',
                name: 'productBrief',
                value: '${product.productBrief}',
                height: 50,
                width: 855
            }, {
                xtype: 'htmleditor',
                fieldLabel: '商品的详细描述',
                name: 'productDesc',
                value: '${product.productDesc}',
                height: 500,
                width: 855
    	        },{
    	            xtype:'textarea',
                fieldLabel: '商家备注（仅商家可见）',
                name: 'sellerNote',
                value: '${product.sellerNote}',
                height: 50,
                width: 855
            }]
        },{
            xtype: 'container',
            layout: 'anchor',
            defaults: {
                margin: '10 0 10 0'
            },
            items: [{
                xtype: 'radiogroup',
                fieldLabel: '是否是实物',
                items: [
                    {boxLabel: '是', name: 'isReal', inputValue: 1, checked: '${product.isReal}'=='1'||'${product.isReal}'==''?true:false},
                    {boxLabel: '否', name: 'isReal', inputValue: 0, checked: '${product.isReal}'=='0'?true:false}
                ],
                anchor:'25%'
            },{
                xtype: 'radiogroup',
                fieldLabel: '是否上架销售',
                items: [
                    {boxLabel: '是', name: 'isOnSale', inputValue: 1, checked: '${product.isOnSale}'=='1'||'${product.isOnSale}'==''?true:false},
                    {boxLabel: '否', name: 'isOnSale', inputValue: 0, checked: '${product.isOnSale}'=='0'?true:false}
                ],
                anchor:'25%'
            },{
                xtype: 'radiogroup',
                fieldLabel: '是否有发票',
                items: [
                    {boxLabel: '有', name: 'hasInvoice', inputValue: 1, checked: '${product.hasInvoice}'=='1'?true:false},
                    {boxLabel: '无', name: 'hasInvoice', inputValue: 0, checked: '${product.hasInvoice}'=='0'||'${product.hasInvoice}'==''?true:false}
                ],
                anchor:'25%'
            },{
                xtype: 'radiogroup',
                fieldLabel: '是否有保修',
                items: [
                    {boxLabel: '有', name: 'hasWarranty', inputValue: 1, checked: '${product.hasWarranty}'=='1'?true:false},
                    {boxLabel: '无', name: 'hasWarranty', inputValue: 0, checked: '${product.hasWarranty}'=='0'||'${product.hasWarranty}'==''?true:false}
                ],
                anchor:'25%'
            }]
        },{
            xtype: 'container',
            width: 340,
            layout:'column',
            style: 'margin: 30px 300px 30px 300px',
            items:[{
	            xtype: 'button',
	            width: 250,
	            margin: '0 5px 0 5px',
	            scale: 'medium',
				text: '我已阅读以下规则，现在发布商品',
				handler: function(){					
					// 提交表单
					var _this = this;					
					_this.disable();
					Ext.getCmp("frmProduct").submit({
					    url: Url.saveProduct,
					    params: {
					    	productId: '${product.productId}'
					    },
						waitTitle : "提示",
						waitMsg : "正在保存...",
					    success: function(form, action) {
					    	window.location.href = "ProductManage.jspx";
					    },
					    failure: function(form, action) {
					    	Ext.formFailure(form, action);
					    	_this.enable();
					    }
					});
				}
			},{
	            xtype: 'button',
	            width: 70,
	            margin: '0 5px 0 5px',
	            scale: 'medium',
				text: '浏览',
				handler: function(){
					
				}
			}]
        }]
        
    });
 
  
 	//------------------------------------------------------------------------------------//

 	
    //界面布局
	Ext.create('Ext.Viewport', {
		id: 'viewport',
	    layout: 'border',
	    title: 'Ext Layout Browser',
	    items: [{
		         region: 'north',
		         border: false,
		         html: '<h1 class="title ${module_icon}-big">${module_title}</h1>',
		         height: 45
			},
			frmProduct
		],
		renderTo: document.body,
        listeners : {
        	afterrender: function(obj, options){
        		//初始化类目树控件
				var cbbCate = Ext.getCmp("cbbCateId"); 	
        		try{
        			cbbCate.expand();
        			cbbCate.collapse();
        		}catch(err){}				
        		Ext.get("treeCategory").parent().parent().removeCls("x-boundlist");
        		
        		//加载类目属性
				Ext.getCmp("productProps").getLoader().load({
                    params: {
                    	cateId: cbbCate.getValue(),
                    	productId: '${product.productId}'
                	}
				});
        		
        		
				Ext.getCmp("cbbBrandId").focus();
        		Ext.getCmp("txtProductName").focus();
			}
		}
	});
 	
});
</script>
</head>
<body>
发布商品
</body>
</html>