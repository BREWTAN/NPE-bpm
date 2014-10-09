var XWATable = function() {
	    
	return {
	    	
	    init: function(xwajson) {


	            var tbform;
	            var restURL;
	            var waTable;
	            var coldefs;
	            var xwaOptions; 
	            var curSeachFilter;
	            var basePath="/nperest";


            var queryURL = function(url){
                // console.log("url=="+url+"::match="+url.match(/.*\?.*/));
                if(url.match(/.*\?.*/)){
                    if(url.match(/\&$/)||url.match(/\?$/)){
                        return url;
                    }
                    else return url+"&";
                }
                return url+"?";
            }
            var handlechosen = function(diag){
                $.each(diag.find("select.chosen-select"), function(index, selEle) {

                    var url=$(selEle).attr("data-ref-url");
                    if(url){
                        var ref_name=$(selEle).attr("data-ref-name");
                        var ref_val=$(selEle).attr("data-ref-value");
                        var data_val=$(selEle).attr("data-value");
                        // console.log("getselected::"+data_val);

                        // console.log("select="+url+":"+ref_name+":"+ref_val);
                        $.ajax({
                            url: queryURL(url)+'fields={"'+ref_name+'":1,"'+ref_val+'":1}',
                            dataType: 'json',
                            async:false
                        })
                        .done(function(data) {
                            // console.log("success::"+JSON.stringify(data));
                            var opts='<option ></option>';
                            console.log("op:"+$(selEle).attr('id')+JSON.stringify(xwaOptions['cols'][$(selEle).attr('id')]))
                            $.each(data, function(index, val) {
                                opts+='<option value="'+val[ref_val]+'"';

                                if(val[ref_val]==data_val) opts+=' selected ';
                                
                                if(val[ref_val] == val[ref_name]||
                                    xwaOptions['cols'][$(selEle).attr('id')]['hidecode']==true){
                                    opts+='>'+val[ref_name]+'</option>'
                                } else {
                                    opts+='>'+val[ref_val]+'('+val[ref_name]+')</option>'
                                }
                            });
                            $(selEle).html(opts);

                        });
                    }
                    console.log("selct::"+$(selEle))
                    $(selEle).chosen({width:"100%"});


                     /* iterate through array or object */
                });
            }

            var handleCustomBuilder= function (diag){
                $.each(diag.find(".editcustom"), function(index, ele) {
                    console.log("builder=="+$(ele).attr("data-builder"));
                    var builder=$(ele).attr("data-builder");
                    var data_val=$(ele).attr("data-value");
                    console.log("call:val="+data_val)
                    var inner=eval(builder+"("+data_val+");");
                    $(ele).replaceWith(inner);
                });
            }

            var handleDatePickers = function (diag) {
                $.each(diag.find(".date-picker"), function(index, ele) {
                    $(ele).datepicker({
                        //rtl: Metronic.isRTL(),
                        autoclose: true
                    });
                    var data_val=$(ele).attr("data-value");
                    if(data_val)
                    {
                        $(ele).children("input").val(data_val);
                    }else{
                        //$(ele).children("input").val($.datepicker.formatDate('yy-mm-dd', new Date()));
                    }

                   });
            };

            var handleDefCtrl=function(diag){
                handleCustomBuilder(diag);

                handleDatePickers(diag);
                handlechosen(diag);
            }

            var addFunction = function() {
                 // bootbox.alert("Hello world!");
                var modjson = JSON.parse(JSON.stringify(coldefs));
                $.each(coldefs, function(index, val) {
                    if(val['new_value'])
                    {
                        modjson[index]['default_value'] = val['new_value'].f();
                    }
                    modjson[index]['readonly']="";
                });
                tbform = Duster.buildArr($('#__dust_tableform'));
                var diag=bootbox.dialog({
                    message: tbform(modjson),
                    title: "添加",
                    buttons: {
                        success: {
                            label: "保存",
                            className: "green",
                            callback: function() {
                                var sendjson = {};
                                $.each($('#__editform input'), function(index, val) {
                                    // console.log(val.name+"::"+val.value);
                                    if (val.name == xwaOptions["key_column"]||!val.name||val.name.length==0) return;
                                    if(val.name == "leaf"){
                                        if($("#leaf").attr("checked")){
                                            sendjson[val.name] = 1;
                                        }else{
                                            sendjson[val.name] = 0;
                                        }
                                    }else{
                                        sendjson[val.name] = val.value;
                                    }
                                    
                                });
                                $.each($('#__editform select'), function(index, val) {
                                    console.log(val.name+"::"+val.value);
                                    if (val.name == xwaOptions["key_column"]||!val.name||val.name.length==0) return;
                                    sendjson[val.name] = val.value;
                                });
                                $.each($('#__editform textarea'), function(index, val) {
                                    console.log(val.name+"::"+val.value);
                                    if (val.name == xwaOptions["key_column"]||!val.name||val.name.length==0) return;
                                    sendjson[val.name] = val.value;
                                });
                                console.log("submit::" + JSON.stringify(sendjson));
                                console.log("restURL=" + restURL);
                                var ret=Restful.insert(restURL, sendjson);
                                if(xwaOptions['onAdded']){
                                    xwaOptions['onAdded'](ret,sendjson);
                                }
                                waTable.update();
                            }
                        },
                        danger: {
                            label: "取消",
                            className: "gray",
                            callback: function() {}
                        },

                    }
                },{show:false,keyboard:false});
                handleDefCtrl(diag);
                console.log("ready to show");
                diag.show();
            };
            var updFunction = function() {
                // bootbox.alert("Hello world!");
                var selrows = waTable.getData(true);
                if (selrows.rows.length == 0) {
                    bootbox.alert("<br><center><h4>请选择需要修改的行</h4></center><br>");
                } else if (selrows.rows.length > 1) {
                    bootbox.alert("<br><center><h4>只能编辑一行</h4></center><br>");
                } else {
                	tbform = Duster.buildArr($('#__dust_tableform'));
                    var selrow = selrows.rows[0];
                    updForRow(selrow);

                }
            };
            var updForRow=function (selrow){
                var modjson = JSON.parse(JSON.stringify(coldefs));
                console.log("modjson::::"+JSON.stringify(coldefs));
                $.each(coldefs, function(index, val) {
                    modjson[index]['default_value'] = selrow[val.col];
                });
                var diag=bootbox.dialog({
                    message: tbform(modjson),
                    title: "修改",
                    buttons: {
                        success: {
                            label: "保存",
                            className: "green",
                            callback: function() {
                                var moditem = {};
                                $.each($('#__editform input'), function(index, val) {
                                    console.log(val.name+"::"+val.value);
                                    if (!val.name||val.name.length==0) return;
                                    if (val.name == "leaf"){
                                        if($("#leaf").attr("checked")){
                                            moditem[val.name] = 1;
                                        }else{
                                           moditem[val.name] = 0;
                                        }
                                    }else{
                                        moditem[val.name] = val.value;
                                    }
                                });
                                $.each($('#__editform select'), function(index, val) {
                                    console.log(val.name+"::"+val.value);
                                    if (val.name == xwaOptions["key_column"]||!val.name||val.name.length==0) return;
                                    moditem[val.name] = val.value;
                                });
                                $.each($('#__editform textarea'), function(index, val) {
                                    console.log(val.name+"::"+val.value);
                                    if (val.name == xwaOptions["key_column"]||!val.name||val.name.length==0) return;
                                    sendjson[val.name] = val.value;
                                });
                                var sendjson = {
                                    "$set": moditem
                                };
                                
                                console.log("submit::" + JSON.stringify(sendjson));
                                Restful.update(restURL,
                                    selrow[xwaOptions["key_column"]], moditem
                                );
                                var ret=waTable.update();

                                if(xwaOptions['onUpdated']){
                                    xwaOptions['onUpdated'](ret,moditem);
                                }
                            }
                        },
                        danger: {
                            label: "取消",
                            className: "gray",
                            callback: function() {}
                        },

                    }
                },{show:false,"keyboard" : true});
                handleDefCtrl(diag);
                console.log("ready to show");
                diag.show();
            }
            var delFunction = function() {
                var selrows = waTable.getData(true);

                if (selrows.rows.length == 0) {
                    bootbox.alert("<br><center><h4>请选择需要删除的行</h4></center><br>");
                } else {
                    var selrow = selrows.rows
                    bootbox.confirm("<br><center><h4>确定删除选择（" + selrows.rows.length + "）条记录吗？</h4></center><br>",
                        function(result) {
                            if (!result) return;
                            var ids=[];
                            $.each(selrow, function(index, row) {
                                ids.push(row[xwaOptions["key_column"]]);
                            });
                            console.log("delete:"+JSON.stringify(ids));
                            var ret=Restful.delByIDS(restURL, ids);

                            waTable.update();
                            if(xwaOptions['onDeleted']){
                                xwaOptions['onDeleted'](ret,ids);
                            }

                        }); //--confirm(aSome)


                } //else
            };

            var findFunction=function(){
                curSeachFilter=xwaOptions.findFilter();
                if(curSeachFilter)
                {
                    waTable.setRestURL(queryURL(restURL)+'query='+curSeachFilter);
                }else{
                    waTable.setRestURL(restURL);
                }
                waTable.update();
            }

            //maqing 2014-09-26 增加自定义页面
            var customFunction = function(formName, formId, title, operatorType) {
               //  bootbox.alert("Hello world!");
               var modjson = JSON.parse(JSON.stringify(coldefs));
               var tbCustomform = Duster.buildArr($(formName));
               var diag;
               
               if (operatorType == "add") {
	               $.each(coldefs, function(index, val) {
	                   if(val['new_value'])
	                   {
	                       modjson[index]['default_value'] = val['new_value'].f();
	                   }
	                   modjson[index]['readonly']="";
	               });
	               diag=bootbox.dialog({
	                   message: tbCustomform(modjson),
	                   title: title,
	                   buttons: {
	                       success: {
	                           label: "保存",
	                           className: "green",
	                           callback: function() {
	                               var sendjson = {};
	                               $.each($('#'+formId+' input'), function(index, val) {
	                                   // console.log(val.name+"::"+val.value);
	                                   if (val.name == xwaOptions["key_column"]||!val.name||val.name.length==0) return;
	                                   if(val.name == "leaf"){
	                                       if($("#leaf").attr("checked")){
	                                           sendjson[val.name] = 1;
	                                       }else{
	                                           sendjson[val.name] = 0;
	                                       }
	                                   }else{
	                                       sendjson[val.name] = val.value;
	                                   }
	                                   
	                               });
	                               $.each($('#'+formId+' select'), function(index, val) {
	                                   console.log(val.name+"::"+val.value);
	                                   if (val.name == xwaOptions["key_column"]||!val.name||val.name.length==0) return;
	                                   sendjson[val.name] = val.value;
	                               });
	
	                               console.log("submit::" + JSON.stringify(sendjson));
	                               console.log("restURL=" + restURL);
	                               var ret=Restful.insert(restURL, sendjson);
	                               if(xwaOptions['onAdded']){
	                                   xwaOptions['onAdded'](ret,sendjson);
	                               }
	                               waTable.update();
	                           }
	                       },
	                       danger: {
	                           label: "取消",
	                           className: "gray",
	                           callback: function() {}
	                       },
	                   }
	               },{show:false,keyboard:false});
               } // end add 
               else {
                   var selrows = waTable.getData(true);
                   if (selrows.rows.length == 0) {
                       bootbox.alert("<br><center><h4>请选择需要修改的行</h4></center><br>");
                   } else if (selrows.rows.length > 1) {
                       bootbox.alert("<br><center><h4>只能编辑一行</h4></center><br>");
                   } else {
                       var selrow = selrows.rows[0];
                       
                       $.each(coldefs, function(index, val) {
                           modjson[index]['default_value'] = selrow[val.col];
                       });
                       var diag=bootbox.dialog({
                           message: tbCustomform(modjson),
                           title: title,
                           buttons: {
                               success: {
                                   label: "保存",
                                   className: "green",
                                   callback: function() {
                                       var moditem = {};
                                       $.each($('#'+formId+' input'), function(index, val) {
                                           console.log(val.name+"::"+val.value);
                                           if (val.name == xwaOptions["key_column"]||!val.name||val.name.length==0) return;
                                           if (val.name == "leaf"){
                                               if($("#leaf").attr("checked")){
                                                   moditem[val.name] = 1;
                                               }else{
                                                  moditem[val.name] = 0;
                                               }
                                           }else{
                                               moditem[val.name] = val.value;
                                           }
                                       });
                                       $.each($('#'+formId+' select'), function(index, val) {
                                           console.log(val.name+"::"+val.value);
                                           if (val.name == xwaOptions["key_column"]||!val.name||val.name.length==0) return;
                                           moditem[val.name] = val.value;
                                       });
                                       var sendjson = {
                                           "$set": moditem
                                       };
                                       
                                       console.log("submit::" + JSON.stringify(sendjson));
                                       Restful.update(restURL,
                                           selrow[xwaOptions["key_column"]], moditem
                                       );
                                       var ret=waTable.update();

                                       if(xwaOptions['onUpdated']){
                                           xwaOptions['onUpdated'](ret,moditem);
                                       }
                                   }
                               },
                               danger: {
                                   label: "取消",
                                   className: "gray",
                                   callback: function() {}
                               },

                           }
                       },{show:false,"keyboard" : true});
                   }
               } //end update
               handleDefCtrl(diag);
               console.log("ready to show");
               diag.show();
           };
            
            
        	xwaOptions=xwajson;
            if(!xwaOptions["key_column"]){
                xwaOptions["key_column"]='_id'
            }
            onNew=xwaOptions.onNew;
            tbform = Duster.buildArr($('#__dust_tableform'));
            restURL = basePath+xwajson.restbase;
            rURL = restURL;
            
            coldefs = xwajson.coldefs;
			var cols={};
            $.each(coldefs, function(index, coldef) {
                cols[coldef.col]=coldef;
            });
            xwajson['cols']=cols;
            xwajson['restURL']=restURL;
            if(!xwajson['rowDblClicked'])
            xwajson['rowDblClicked']=function(e){
            	console.log("dblclickedd::"+JSON.stringify(e.row));
            	updForRow(e.row);
            }
//-----------------READY------
            if(xwajson['divname']){
    		  waTable = $('#'+xwajson['divname']).WATable(xwajson).data('WATable'); 
            }
            else{
             waTable = $('#divtable').WATable(xwajson).data('WATable');    
            }
            // console.log("addFunction=="+$('#add_button'));
            if(xwajson['addbtn']) $('#'+xwajson['addbtn']).click(addFunction);
            else $('#add_button').click(addFunction);
            
            if(xwajson['updbtn']) $('#'+xwajson['updbtn']).click(addFunction);
            else $('#upd_button').click(updFunction); //update diag
            
            if(xwajson['delbtn']) $('#'+xwajson['delbtn']).click(addFunction);
            else $('#del_button').click(delFunction); //--del-diag
            
            if(xwajson['findbtn']) $('#'+xwajson['findbtn']).click(addFunction);
            else $('#find_button').click(findFunction);//find-button

            //maqing 2014-09-26 增加自定义按钮
            var customForms  = xwajson.customForms;
            if (customForms) {
	            $.each(customForms, function(index, customForm) {
	                $('#'+customForm.btnId).bind('click', customForm, function(event)
	                		{
	                			customFunction(event.data.formName,event.data.formId, 
	                					event.data.title, event.data.operateType);
	                		} ); 
	            });
            }
            
           
            // console.log("inpuss::"+.length);
            $.each($("input.inputclear"), function(index, inputele) {
				var input=$(inputele);            	
				var clear=input.next('span.inputclear');
				clear.removeClass('hide');
			 	input.keyup(function() {
			        clear.toggle(Boolean($(this).val()));
			    });    
			    clear.toggle(Boolean(input.val()));
			    clear.click(function() {
			        input.val('').focus();
			        input.trigger('input')
			        $(this).hide();
			     });

            });
            
            return  {
                update: function(){
                    waTable.update();
                },
                autoResetSearch:function(){
                    curSeachFilter=xwaOptions.findFilter();
                    if(curSeachFilter)
                    {
                        waTable.setRestURL(queryURL(restURL)+'query='+curSeachFilter);
                    }else{
                        waTable.setRestURL(restURL);
                    }
                    waTable.update();
                },
                setRestURL:function(url){
                    restURL=url;
                },
                getRestURL:function(){
                    return  rURL;
                },
                getTable:function(){
                    return waTable;
                },
                getXWAOptions:function(){
                    return xwaOptions;
                },


            };
        },
        
    } //---xwatable-return;
}();

if($("#divtable")){
    $("#divtable").addClass('container')
}

