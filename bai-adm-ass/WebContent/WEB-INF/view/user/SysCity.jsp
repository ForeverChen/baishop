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
       	getCity: '${page_action}?func=getCity',
       	delCity: '${page_action}?func=delCity',
       	saveCity: '${page_action}?func=saveCity'
    };

    
 	//------------------------------------------------------------------------------------//
 	
    
	//城市添加/编辑窗口
	var winCity = Ext.create('widget.window', {
		id: 'winCity',
		title: '城市信息',
		width: 280,
		height: 285,
		layout: 'fit',
		closable: true,
		closeAction: 'hide',
		modal: true,
		resizable: false,
		items: [Ext.create('Ext.form.Panel', {
			id: 'frmCity',
			bodyStyle:'padding:20px 0 0 23px',
			border: false,
			autoScroll: true,
			fieldDefaults: {
				labelAlign: 'left',
				labelWidth: 50, 
				msgTarget: 'side',
              	autoFitErrors: false, 
				width: 235
			},
			items: [{
			    xtype: 'hiddenfield',
			    id: 'hddId',
			    name: 'city.id',
			    value: ''
			},{
			    xtype: 'hiddenfield',
			    id: 'hddPid',
			    name: 'city.pid',
			    value: ''
			},{
			    xtype:'textfield',
			    id: 'txtName',
			    name: 'city.name',
			    fieldLabel: '城市',
			    allowBlank: false
			},{
			    xtype:'textfield',
			    id: 'txtCode',
			    name: 'city.code',
			    fieldLabel: '编号',
			    allowBlank: false
			},{
				xtype:'textfield',
				id: 'txtPinyin',
				name: 'city.pinyin',
				fieldLabel: '拼音'
			},{
			    xtype:'textarea',
			    id: 'txtRemark',
			    name: 'city.remark',
			    fieldLabel: '备注'
			}]
		})],
		buttons: [{
			id: "btnOK",
		    text:'确定',
		    width: 80,
		    handler: function(){			    		 		
		    	saveCity(Ext.getCmp("winCity"), Ext.getCmp("frmCity"), Ext.getCmp("treeCity"));
		    }
		},{
			id: "btnCancel",
		    text:'取消',
		    width: 80,
		    handler: function(){
		    	Ext.getCmp("winCity").hide();
		    }
		}]
	});
    

 	//------------------------------------------------------------------------------------//
    
    var treeCity = Ext.create('Ext.tree.Panel', {
		id: 'treeCity',
      	region: 'center',
		border: true,
		split:true,
        animate: true,
		autoscroll: true,
		useArrows: true,
		multiSelect: false,
		singleExpand: false,
		rootVisible: true,
		store: Ext.create('Ext.data.TreeStore', {
	  	    idProperty: 'id',
			fields: [
				'id', 'pid', 'code', 'name', 'pinyin', 'remark'
			],
			root: {
				id: 0,
				text: "行政区划",
				id: 0,
				name: "行政区划",
				code: "",
				iconCls: 'icon-city-group',
				expanded: true
			},
			autoSync: true,
	        proxy: {
	            type: 'ajax',
	            url: Url.getCity,
				listeners: {
	    			exception: function(proxy, request, operation, options) {
	    				Ext.Msg.alert("提示", "加载数据出错："+proxy.getReader().rawData['msg'] );
	    			}
	    		}
	        }
	    }),
        columns: [{
            xtype: 'treecolumn',
            text: '城市',
            dataIndex: 'name',
            width: 300,
          	sortable: false
        },{
            text: '编号',
            dataIndex: 'code',
            width: 150,
          	sortable: false
        },{
            text: '拼音',
            dataIndex: 'pinyin',
            width: 150,
          	sortable: false
        },{
            text: '备注',
            dataIndex: 'remark',
            width: 200,
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
			handler: function(btn, event) {
				//判断是否选择一条记录
				var rows = Ext.getCmp("treeCity").selModel.getSelection();
				if (rows.length != 1) {
					Ext.Msg.alert("提示", "请选择一条记录");
					return;
				}				
				
				//显示窗口
				winCity.show(this,function(){
					this.setTitle("添加城市");
					var frmCity = Ext.getCmp("frmCity");
					frmCity.getForm().reset();
					frmCity.items.get("txtName").focus();
					frmCity.items.get("hddId").setValue(null);
					frmCity.items.get("hddPid").setValue(rows[0].get("id"));
					frmCity.items.get("txtCode").setValue(rows[0].get("code"));		
				});
			}
		},{
			id: 'btnEdit',
			text: '编辑',
			iconCls: 'icon-edit',
			handler: function(btn, event) {
				//判断是否选择一条记录
				var rows = Ext.getCmp("treeCity").selModel.getSelection();
				if (rows.length != 1) {
					Ext.Msg.alert("提示", "请选择一条记录");
					return;
				}
	    	
				//显示窗口
				winCity.show(this,function(){
					this.setTitle("编辑城市");
					var frmCity = Ext.getCmp("frmCity");
					frmCity.items.get("txtName").focus();
					frmCity.items.get("hddId").setValue(rows[0].get("id"));
					frmCity.items.get("hddPid").setValue(rows[0].get("pid"));
					frmCity.items.get("txtName").setValue(rows[0].get("name"));
					frmCity.items.get("txtCode").setValue(rows[0].get("code"));
					frmCity.items.get("txtPinyin").setValue(rows[0].get("pinyin"));
					frmCity.items.get("txtRemark").setValue(rows[0].get("remark"));		
				});
			}
		},{
			text: '删除',
			iconCls: 'icon-del',
			handler: function(btn, event) {
				delCity(Ext.getCmp("treeCity"));
			}
		}]
	});
    
    
	//------------------------------------------------------------------------------------//
 	    
	
	//保存城市
	function saveCity(win, frm, tree){
		// 提交表单
		frm.submit({
		    url: Url.saveCity,
			waitTitle : "提示",
			waitMsg : "正在保存...",
		    success: function(form, action) {
		    	win.hide();
		    	
		    	var node = tree.store.getNodeById(frm.items.get("hddDeptPid").getValue());
		    	tree.store.load({
		    		node: node
		    	});
		    	node.expand(true);
		    },
		    failure: function(form, action) {
		    	Ext.formFailure(form, action);
		    }
		});
	}
		
		
	//删除城市
	function delCity(tree){
    	var rows = tree.selModel.getSelection();
    	if (rows.length > 0) {
    		Ext.Msg.show({
    			title : '提示',
    			msg : '删除城市将会删除该城市下的所有节点数据，确定要将选择的城市删除吗？',
    			buttons : Ext.Msg.OKCANCEL,
    			icon : Ext.MessageBox.QUESTION,
    			fn : function(btn, text) {
    				if (btn == 'ok') {
    					// 构建Ajax参数
    					var id = rows[0].get('id');
    					var pid = rows[0].get('pid');
    					
    					// 发送请求
    					tree.el.mask("正在删除城市...", 'x-mask-loading');
    					Ext.Ajax.request({
    						url : Url.delCity,
    						params : "id=" + id,
    						method : "POST",
    						waitMsg : "正在删除...",
    						success : function(response, options) {
    							tree.el.unmask();
    							var json = Ext.JSON.decode(response.responseText);
								if (json.success) {
									tree.store.load({
							    		node: tree.store.getNodeById(pid)
							    	});
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
    		Ext.Msg.alert("提示", "请选择城市");
    	}
	}
    
    
 	//------------------------------------------------------------------------------------//

 	
    //界面布局
	Ext.create('Ext.Viewport', {
		id: 'viewport',
	    layout: 'border',
	    title: '${module_title}',
	    items: [treeCity],
		renderTo: document.body
	});
 		
	
});
</script>
</head>
<body>
    <!-- 界面加载 -->
    <div id="loading"><span class="title"></span><span class="logo"></span></div>
</body>
</html>