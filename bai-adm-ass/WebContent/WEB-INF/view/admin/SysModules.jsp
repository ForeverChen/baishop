<?xml version="1.0" encoding="UTF-8" ?>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<%@ include file="../meta.jsp" %>
<title>${module_title}</title>
<script type="text/javascript">
Ext.onReady(function() {
    Ext.tip.QuickTipManager.init();
    
    //业务请求的URL
    Url = {
       	getModules: '${page_context}/admin/SysModules.jspx?func=getModules',
       	delModules: '${page_context}/admin/SysModules.jspx?func=delModules',
       	saveModules: '${page_context}/admin/SysModules.jspx?func=saveModules',
       	upModules: '${page_context}/admin/SysModules.jspx?func=upModules',
       	downModules: '${page_context}/admin/SysModules.jspx?func=downModules',
       	getRoles: '${page_context}/admin/SysRoles.jspx?func=getRoles&action=SysModules',
        getRolesByModules: '${page_context}/admin/SysModules.jspx?func=getRolesByModules',
        getUsersByModules: '${page_context}/admin/SysModules.jspx?func=getUsersByModules'
    };

    
 	//------------------------------------------------------------------------------------//
 	
    
	//应用模块添加/编辑窗口
	var winModules = Ext.create('widget.window', {
		id: 'winModules',
		title: '应用模块信息',
		width: 350,
		height: 255,
		layout: 'fit',
		closable: true,
		closeAction: 'hide',
		modal: true,
		resizable: false,
		items: [Ext.create('Ext.form.Panel', {
			id: 'frmModules',
			bodyStyle:'padding:20px 0 0 23px',
			border: false,
			autoScroll: true,
			defaults: { 
				listeners: {
					specialkey: function(obj,e){
						 if (e.getKey() == Ext.EventObject.ENTER) {
							Ext.getCmp("btnOK").handler();
						}
					}
				}
			},
			fieldDefaults: {
				labelAlign: 'left',
				labelWidth: 60, 
				msgTarget: 'side',
              	//autoFitErrors: false, 
				width: 280
			},
			items: [{
			    xtype: 'hiddenfield',
			    id: 'hddModuleId',
			    name: 'module.moduleId',
			    value: ''
			},{
			    xtype: 'hiddenfield',
			    id: 'hddModulePid',
			    name: 'module.modulePid',
			    value: ''
			},{
			    xtype: 'hiddenfield',
			    id: 'hddType',
			    name: 'module.type',
			    value: ''
			},
			{
				id: 'conCode',
				xtype: 'container',
			    layout: 'table',
			    items:[{
				    xtype:'textfield',
				    id: 'txtCode',
				    name: 'module.code',
				    fieldLabel: '编码',
					width: 235,
				    readOnly: true
				},{
					xtype: 'button',
				    text:'变更',
					width: 40,
					style:'margin:0 0 5px 5px',
				    handler: function(){
				    	var txtCode = Ext.getCmp("frmModules").items.get('conCode').items.get("txtCode");
				    	txtCode.setValue(txtCode.getValue().replace(/.{2}$/, getRandomCode()));
				    }
				}]
			},{
			    xtype:'textfield',
			    id: 'txtModuleText',
			    name: 'module.text',
			    fieldLabel: '模块名称',
			    allowBlank: false
			},{
			    xtype:'textfield',
			    id: 'txtUrl',
			    name: 'module.url',
			    fieldLabel: 'URL地址'
			},{
		        xtype: 'filefield',
		        id: 'fileImage',
			    name: 'module.image',
		        fieldLabel: '图片',
		        msgTarget: 'side',
		        buttonText: '选择图片'
		    },{
				xtype:'combobox',
				id: 'cbbExpanded',
				name: 'module.expanded',
				fieldLabel: '是否展开',
				editable: false,
				store: [[1,'是'],[0,'否']],
				value: 1
			},{
			    xtype:'textfield',
			    id: 'txtDescription',
			    name: 'module.description',
			    fieldLabel: '描述'
			}]
		})],
		buttons: [{
			id: "btnOK",
		    text:'确定',
		    width: 80,
		    handler: function(){			    		 		
		    	saveModules(Ext.getCmp("winModules"), Ext.getCmp("frmModules"), Ext.getCmp("treeModules"));
		    }
		},{
			id: "btnCancel",
		    text:'取消',
		    width: 80,
		    handler: function(){
		    	Ext.getCmp("winModules").hide();
		    }
		}]
	});
 	
 	
	//查看用户窗口
    var winModulesAdmins = Ext.create('widget.window', {
		id: 'winModulesAdmins',
		title: '查看用户',
		width: 400,
		height: 450,
		layout: 'fit',
		closable: true,
		closeAction: 'hide',
		modal: true,
		resizable: false,
		buttonAlign: 'center',
		userId: null,
		items: [Ext.create('Ext.grid.Panel', {
			id: 'gridModulesAdmins',
			border: false,
			disableSelection: false,
			loadMask: true,
			store: Ext.create('Ext.data.Store', {
		        idProperty: 'userId',
		        fields: [
					'userId', 'username', 'name', 'code'
		        ],
		        proxy: {
		            type: 'jsonp',
		            url: Url.getUsersByModules,
		            reader: {
		                root: 'records'
		            },
		            simpleSortMode: true,
					listeners: {
		    			exception: function(proxy, request, operation, options) {
		    				Ext.Msg.alert("提示", "加载数据出错："+ (proxy.getReader().rawData['msg'] || "未处理异常") );
		    			}
		    		}
		        }
		    }),
	        columns:[Ext.create('Ext.grid.RowNumberer', {width:30}), {
	            text: '用户名',
	            dataIndex: 'username',
	            width: 140,
	            sortable: true
	        },{
	            text: '姓名',
	            dataIndex: 'name',
	            width: 190,
	            sortable: true,
	            renderer: function(value, p, record) {
	            	return "["+ record.get("code") +"] " + value;
				}
	        }]
		})]
	});
 	
 	
	//查看角色窗口
    var winModulesRoles = Ext.create('widget.window', {
		id: 'winModulesRoles',
		title: '查看角色',
		width: 355,
		height: 450,
		layout: 'fit',
		closable: true,
		closeAction: 'hide',
		modal: true,
		resizable: false,
		buttonAlign: 'center',
		userId: null,
		items: [Ext.create('Ext.tree.Panel', {
			id: 'treeModulesRoles',
	        useArrows: true,
	        rootVisible: false,
	        store: Ext.create('Ext.data.TreeStore', {
	            proxy: {
	                type: 'ajax',
	                url: Url.getRoles
	            }
	        }),
			viewConfig : {
				onCheckboxChange : function(e, t) {
				    return false;
				}
			}
	    })]
	});
    

 	//------------------------------------------------------------------------------------//
    
    var treeModules = Ext.create('Ext.tree.Panel', {
		id: 'treeModules',
		region: 'center',
		border: true,
		split:true,
        animate: true,
		autoscroll: true,
		useArrows: true,
		multiSelect: false,
		singleExpand: false,
		rootVisible: false,
		store: Ext.create('Ext.data.TreeStore', {
	  	    idProperty: 'moduleId',
			fields: [
				'moduleId', 'modulePid', 'text', 'code', 'type', 'url', 'image', 'description', 'expanded', 'sort'
			],
	        proxy: {
	            type: 'ajax',
	            url: Url.getModules,
				listeners: {
	    			exception: function(proxy, request, operation, options) {
	    				Ext.Msg.alert("提示", "加载数据出错："+ (proxy.getReader().rawData['msg'] || "未处理异常") );
	    			}
	    		}
	        }
	    }),
        columns: [{
            xtype: 'treecolumn',
            text: '子系统、模块组、应用模块',
            dataIndex: 'text',
            width: 300,
          	sortable: false
        },{
            text: '编码',
            dataIndex: 'code',
            width: 150,
          	sortable: false
        },{
            text: '模块URL、功能URL、常量值',
            dataIndex: 'url',
            width: 400,
          	sortable: false
        }],
        listeners : {
        	itemdblclick : function(view, record, item, index, e, options){
        		Ext.getCmp("btnEdit").handler();	
        	}
        },
		tbar: [{
			text: '添加',
			iconCls: 'icon-add',
			menu: [{
				text: '子系统',
				iconCls: 'icon-sub-system',
				handler: function(btn, event) {					
					//显示窗口
					winModules.show(this,function(){
						this.setTitle("添加子系统");
						this.setHeight(205);
						var frmModules = Ext.getCmp("frmModules");
						frmModules.getForm().reset();
						frmModules.items.get("txtModuleText").setFieldLabel("子系统");
						frmModules.items.get("hddModuleId").setValue(null);
						frmModules.items.get("hddModulePid").setValue(0);
						frmModules.items.get("hddType").setValue("SYSTEM");
						frmModules.items.get('conCode').items.get("txtCode").setValue(getRandomCode());
						frmModules.items.get("txtUrl").setVisible(false);
						frmModules.items.get("fileImage").setVisible(false);
						frmModules.items.get("cbbExpanded").setVisible(false);
					});
				}
			},{
				text: '模块组',
				iconCls: 'icon-module-group',
				handler: function(btn, event) {
					//判断是否选择一条记录
					var rows = Ext.getCmp("treeModules").selModel.getSelection();
					if (rows.length==1 && (rows[0].data.type=="SYSTEM" || rows[0].data.type=="GROUP")) {	
						//显示窗口
						winModules.show(this,function(){
							this.setTitle("添加模块组");
							this.setHeight(233);
							var frmModules = Ext.getCmp("frmModules");
							frmModules.getForm().reset();
							frmModules.items.get("txtModuleText").setFieldLabel("模块组");
							frmModules.items.get("hddModuleId").setValue(null);
							frmModules.items.get("hddModulePid").setValue(rows[0].get("moduleId"));
							frmModules.items.get("hddType").setValue("GROUP");
							frmModules.items.get('conCode').items.get("txtCode").setValue(rows[0].get("code") + getRandomCode());
							frmModules.items.get("txtUrl").setVisible(false);
							frmModules.items.get("fileImage").setVisible(false);
							frmModules.items.get("cbbExpanded").setVisible(true);	
						});
					}else{
						Ext.Msg.alert("提示", "请选择子系统或模块组记录");
						return;
					}
				}
			},{
				text: '应用模块',
				iconCls: 'icon-module-app',
				handler: function(btn, event) {
					//判断是否选择一条记录
					var rows = Ext.getCmp("treeModules").selModel.getSelection();
					if (rows.length==1 && (rows[0].data.type=="SYSTEM" || rows[0].data.type=="GROUP")) {					
						//显示窗口
						winModules.show(this,function(){
							this.setTitle("添加应用模块");
							this.setHeight(263);
							var frmModules = Ext.getCmp("frmModules");
							frmModules.getForm().reset();
							frmModules.items.get("txtModuleText").setFieldLabel("应用模块");
							frmModules.items.get("txtUrl").setFieldLabel("URL");
							frmModules.items.get("hddModuleId").setValue(null);
							frmModules.items.get("hddModulePid").setValue(rows[0].get("moduleId"));
							frmModules.items.get("hddType").setValue("MODULE");
							frmModules.items.get('conCode').items.get("txtCode").setValue(rows[0].get("code") + getRandomCode());
							frmModules.items.get("txtUrl").setVisible(true);
							frmModules.items.get("fileImage").setVisible(true);
							frmModules.items.get("cbbExpanded").setVisible(false);
						});
					}else{
						Ext.Msg.alert("提示", "请选择子系统或模块组记录");
						return;
					}
				}
			},{
				text: '操作功能',
				iconCls: 'icon-module-function',
				handler: function(btn, event) {
					//判断是否选择一条记录
					var rows = Ext.getCmp("treeModules").selModel.getSelection();
					if (rows.length==1 && rows[0].data.type=="MODULE") {					
						//显示窗口
						winModules.show(this,function(){
							this.setTitle("添加操作功能");
							this.setHeight(233);
							var frmModules = Ext.getCmp("frmModules");
							frmModules.getForm().reset();
							frmModules.items.get("txtModuleText").setFieldLabel("功能名");
							frmModules.items.get("txtUrl").setFieldLabel("URL");
							frmModules.items.get("hddModuleId").setValue(null);
							frmModules.items.get("hddModulePid").setValue(rows[0].get("moduleId"));
							frmModules.items.get("hddType").setValue("FUNCTION");
							frmModules.items.get('conCode').items.get("txtCode").setValue(rows[0].get("code") + getRandomCode());
							frmModules.items.get("txtUrl").setVisible(true);
							frmModules.items.get("fileImage").setVisible(false);
							frmModules.items.get("cbbExpanded").setVisible(false);
						});
					}else{
						Ext.Msg.alert("提示", "请选择应用模块记录");
						return;
					}
				}
			}]
		},{
			id: 'btnEdit',
			text: '编辑',
			iconCls: 'icon-edit',
			handler: function(btn, event) {
				//判断是否选择一条记录
				var rows = Ext.getCmp("treeModules").selModel.getSelection();
				if (rows.length != 1) {
					Ext.Msg.alert("提示", "请选择一条记录");
					return;
				}
	    	
				//显示窗口
				winModules.show(this,function(){
					var frmModules = Ext.getCmp("frmModules");
					frmModules.items.get("hddModuleId").setValue(rows[0].get("moduleId"));
					frmModules.items.get("hddModulePid").setValue(rows[0].get("modulePid"));
					frmModules.items.get("hddType").setValue(rows[0].get("type"));
					frmModules.items.get("txtModuleText").setValue(rows[0].get("text"));
					frmModules.items.get("txtUrl").setValue(rows[0].get("url"));
					frmModules.items.get("fileImage").setRawValue(rows[0].get("image"));
					frmModules.items.get("txtDescription").setValue(rows[0].get("description"));
					frmModules.items.get("cbbExpanded").setValue(rows[0].data.nExpanded!=null? rows[0].data.nExpanded : rows[0].raw.nExpanded);
					
					if(rows[0].get("code")){
						frmModules.items.get('conCode').items.get("txtCode").setValue(rows[0].get("code"));
					}else{
						if(rows[0].parentNode && rows[0].parentNode.get("code")){
							frmModules.items.get('conCode').items.get("txtCode").setValue(rows[0].parentNode.get("code") + getRandomCode());
						}else{
							frmModules.items.get('conCode').items.get("txtCode").setValue(getRandomCode());
						}
					}
					
					switch(rows[0].data.type){
					case "SYSTEM":
						this.setTitle("编辑子系统");
						this.setHeight(205);
						frmModules.items.get("txtModuleText").setFieldLabel("子系统");
						frmModules.items.get("txtUrl").setVisible(false);
						frmModules.items.get("fileImage").setVisible(false);
						frmModules.items.get("cbbExpanded").setVisible(false);
						break;
					case "GROUP":
						this.setTitle("编辑模块组");
						this.setHeight(233);
						frmModules.items.get("txtModuleText").setFieldLabel("模块组");
						frmModules.items.get("txtUrl").setVisible(false);
						frmModules.items.get("fileImage").setVisible(false);
						frmModules.items.get("cbbExpanded").setVisible(true);
						break;
					case "MODULE":
						this.setTitle("编辑应用模块");
						this.setHeight(263);
						frmModules.items.get("txtModuleText").setFieldLabel("应用模块");
						frmModules.items.get("txtUrl").setFieldLabel("URL");
						frmModules.items.get("txtUrl").setVisible(true);
						frmModules.items.get("fileImage").setVisible(true);
						frmModules.items.get("cbbExpanded").setVisible(false);	
						break;
					case "FUNCTION":
						this.setTitle("编辑操作功能");
						this.setHeight(233);
						frmModules.items.get("txtModuleText").setFieldLabel("功能名");
						frmModules.items.get("txtUrl").setFieldLabel("URL");
						frmModules.items.get("txtUrl").setVisible(true);
						frmModules.items.get("fileImage").setVisible(false);
						frmModules.items.get("cbbExpanded").setVisible(false);	
						break;
					}					
				});
			}
		},{
			text: '删除',
			iconCls: 'icon-del',
			handler: function(btn, event) {
				delModules(Ext.getCmp("treeModules"));
			}
		},'-',{
			text: '上移',
			iconCls: 'icon-up',
			handler: function(btn, event) {
				upModules(Ext.getCmp("treeModules"));
			}
		},{
			text: '下移',
			iconCls: 'icon-down',
			handler: function(btn, event) {
				downModules(Ext.getCmp("treeModules"));				
			}
		},'-',{
			text: '展开',
			iconCls: 'icon-up',
			handler: function(btn, event) {
				window.setTimeout(function(){
				    Ext.getCmp("treeModules").expandAll();
				}, 200);
			}
		},{
			text: '合并',
			iconCls: 'icon-down',
			handler: function(btn, event) {
				window.setTimeout(function(){
				    Ext.getCmp("treeModules").collapseAll();
				}, 200);	
			}
		},'-',{
            text: '查看用户',
            iconCls: 'icon-search',
		    handler: function(btn, event) {
				//判断是否选择一条记录
				var rows = Ext.getCmp("treeModules").selModel.getSelection();
				if (rows.length != 1) {
					Ext.Msg.alert("提示", "请选择一条记录");
					return;
				}
		    	
		    	winModulesAdmins.show(this,function(_this, options){
		    		winModulesAdmins.items.get("gridModulesAdmins").getStore().load({
		    			params:{
		    				moduleId: rows[0].get('moduleId')
		    			}
		    		});
		    	});
		    }
        },{
            text: '查看角色',
            iconCls: 'icon-search',
		    handler: function(btn, event) {
				//判断是否选择一条记录
				var rows = Ext.getCmp("treeModules").selModel.getSelection();
				if (rows.length != 1) {
					Ext.Msg.alert("提示", "请选择一条记录");
					return;
				}
		    	
		    	winModulesRoles.show(this,function(_this, options){
		    		Ext.Ajax.request({
						url : Url.getRolesByModules,
						params : "moduleId=" + rows[0].get('moduleId'),
						method : "POST",
						waitMsg : '正在载入数据...',
						success : function(response, options) {
		                	//勾选角色列表
				    		var roleIds = Ext.decode(response.responseText);
				    		winModulesRoles.items.get("treeModulesRoles").getStore().getRootNode().cascadeBy(function(node){
								if(node.raw){
									node.set('checked', false);
									for(var i=0;i<roleIds.length;i++){
										if(node.raw.roleId==Number(roleIds[i])){
											node.set('checked', true);
											node.parentNode.set('checked', true);
											break;
										}
									}
								}
							});	  
						},
						failure : function(response, options) {
							Ext.ajaxFailure(response, options);
						}
					});
		    	});
		    }
        }]
	});
    
    
	//------------------------------------------------------------------------------------//
 	
	
	//保存应用模块
	function saveModules(win, frm, tree){
		// 提交表单
		frm.submit({
		    url: Url.saveModules,
			waitTitle : "提示",
			waitMsg : "正在保存...",
		    success: function(form, action) {
		    	win.hide();
		    	
		    	var id = tree.selModel.getSelection()[0].data.id;
		    	var node;
		    	if(id==0){
		    		node = tree.store.getRootNode();
		    	}else{
		    		node = tree.store.getNodeById(id);	
		    	}
		    	
				if(frm.items.get("hddModuleId").getValue()==null || frm.items.get("hddModuleId").getValue()==""){
		    		//添加
			    	action.result.module.id = action.result.module.moduleId;
			    	action.result.module.leaf = true;
			    	action.result.module.nExpanded = 1;
		    		node.set('leaf', false);
		    		node.appendChild(action.result.module);
		    		node.expand();
		    	}else{
		    		//编辑
		    		node.set('text', action.result.module.text);
		    		node.set('code', action.result.module.code);
		    		node.set('url', action.result.module.url);
		    		node.set('description', action.result.module.description);
		    		node.set('nExpanded', action.result.module.expanded);
		    	}
		    },
		    failure: function(form, action) {
		    	Ext.formFailure(form, action);
		    }
		});
	}
		
		
	//删除应用模块
	function delModules(tree){
    	var rows = tree.selModel.getSelection();
    	if (rows.length > 0) {
    		Ext.Msg.show({
    			title : '提示',
    			msg : '确定要将选择的应用模块删除吗？',
    			buttons : Ext.Msg.OKCANCEL,
    			icon : Ext.MessageBox.QUESTION,
    			fn : function(btn, text) {
    				if (btn == 'ok') {
    					// 构建Ajax参数
    					var ajaxparams = "moduleId=" + rows[0].get('moduleId');
    					
    					// 发送请求
    					tree.el.mask("正在删除应用模块...", 'x-mask-loading');
    					Ext.Ajax.request({
    						url : Url.delModules,
    						params : ajaxparams,
    						method : "POST",
    						waitMsg : "正在删除...",
    						success : function(response, options) {
    							tree.el.unmask();
    							var json = Ext.JSON.decode(response.responseText);
								if (json.success) {
									var node = tree.selModel.getSelection()[0];
									node.remove();
									tree.selModel.select(0);
								}else{
									Ext.Msg.alert("提示", json.msg);
								}
    						},
    						failure : function(response, options) {
    							tree.el.unmask();
    							Ext.ajaxFailure(response, options);
    						}
    					});
    				}
    			}
    		});

    	} else {
    		Ext.Msg.alert("提示", "请选择应用模块");
    	}
	}
	
	
	//上移应用模块
	function upModules(tree){
    	var rows = tree.selModel.getSelection();
    	if (rows.length > 0) {
    		//找到上一个节点
			var node = tree.store.getNodeById(rows[0].get('moduleId'));
			var prevModuleId = "";
			for(var i=0;i<node.parentNode.childNodes.length;i++){
				if(node.parentNode.childNodes[i]==node){
					if(i-1<0){
						return;
					}else{
						prevModuleId = node.parentNode.childNodes[i-1].data.id;
						break;
					}
				}
			}    					

			// 构建Ajax参数
			var ajaxparams = "moduleId=" + rows[0].get('moduleId') + "&prevModuleId=" + prevModuleId;    					
			
			// 发送请求
			tree.el.mask("正在上移应用模块...", 'x-mask-loading');
			Ext.Ajax.request({
				url : Url.upModules,
				params : ajaxparams,
				method : "POST",
				waitMsg : "正在上移...",
				success : function(response, options) {
					tree.el.unmask();
					var json = Ext.JSON.decode(response.responseText);
					if (json.success) {
						node.parentNode.insertBefore(node,node.previousSibling);
					}else{
						Ext.Msg.alert("提示", json.msg);
					}
				},
				failure : function(response, options) {
					tree.el.unmask();
					Ext.ajaxFailure(response, options);
				}
			});

    	} else {
    		Ext.Msg.alert("提示", "请选择应用模块");
    	}
	}
	
	//下移应用模块
	function downModules(tree){
    	var rows = tree.selModel.getSelection();
    	if (rows.length > 0) {
    		//找到下一个节点
			var node = tree.store.getNodeById(rows[0].get('moduleId'));
			var nextModuleId = "";
			for(var i=0;i<node.parentNode.childNodes.length;i++){
				if(node.parentNode.childNodes[i]==node){
					if(i+1>=node.parentNode.childNodes.length){
						return;
					} else {    							
						nextModuleId = node.parentNode.childNodes[i+1].data.id;
						break;
					}
				}
			}    					

			// 构建Ajax参数
			var ajaxparams = "moduleId=" + rows[0].get('moduleId') + "&nextModuleId=" + nextModuleId;    					
			
			// 发送请求
			tree.el.mask("正在下移应用模块...", 'x-mask-loading');
			Ext.Ajax.request({
				url : Url.downModules,
				params : ajaxparams,
				method : "POST",
				waitMsg : "正在下移...",
				success : function(response, options) {
					tree.el.unmask();
					var json = Ext.JSON.decode(response.responseText);
					if (json.success) {
						node.parentNode.insertBefore(node.nextSibling, node);
					}else{
						Ext.Msg.alert("提示", json.msg);
					}
				},
				failure : function(response, options) {
					tree.el.unmask();
					Ext.ajaxFailure(response, options);
				}
			});

    	} else {
    		Ext.Msg.alert("提示", "请选择应用模块");
    	}
	}
	
	
	//获取两位字符的code
	var CODE_SEED = new Array('A','B','C','D','E','F','G','H','I','G','K','L','M','N','O','P','Q','R','S','T','U','V','W','X','Y','Z');
	function getRandomCode(){
		return CODE_SEED[Math.round(Math.random()*25)]+CODE_SEED[Math.round(Math.random()*25)];
	}
    
    
 	//------------------------------------------------------------------------------------//

 	
    //界面布局
	Ext.create('Ext.Viewport', {
		id: 'viewport',
	    layout: 'border',
	    title: '${module_title}',
	    items: [treeModules],
		renderTo: document.body
	});

});
</script>
</head>
<body>
</body>
</html>