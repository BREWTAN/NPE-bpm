var Boards = function() {
	 var xjson;
	 var rURL;
	 var central_data=[];


	    var colors = ['#68BC31','#2091CF','#AF4E96','#DA5430','#FEE074']

	    
	return {
	    	
	     drawPie:function(){
	     
	          var placeholder = $('#piechart-placeholder').css({'width':'90%' , 'min-height':'150px'});
	         
	    var central_sums=[0,0,0,0,0,0]

		$.ajax({
					url: '/nperest/qstatbycenter',
					type: 'GET',
					dataType: 'JSON',
				})
				.done(function(data) {
					// console.log("getQ::"+JSON.stringify(data))
					var keymap={};
					$.each(data,function(index,row){
						var key=row['taskname'];
						if(key==null||key=="")key="默认"
						if(!keymap[key]){
							keymap[key]={}
						}
						keymap[key][row['interstate']]=row['counter']
					});
					var rawdata=[]
					$.each(keymap,function(key,value){
						//console.log("::"+JSON.stringify(value))
						if(!key||key=="null")key="默认"
						var v=[0,0,0,0]
						for(var i=0;i<v.length;i++)if(value[i])v[i]=value[i]
						
						rawdata.push({"name":key,"newcc":v[0],"obtaincc":v[1],"submitcc":v[2],
							"termcc":v[3]})
					})

					console.log(JSON.stringify(rawdata))
				
				  
			
				central_data=rawdata;
	          	 $.each(rawdata, function(index, row) {
					central_sums[1]+= row["newcc"]
					central_sums[2]+=row["obtaincc"]
					central_sums[0]+=row["submitcc"]
					central_sums[3]+=row["termcc"]

	          	 })
	          	 $("#cc_new").html(central_sums[0])
	          	 $("#cc_obtain").html(central_sums[1])
	          	 $("#cc_submit").html(central_sums[2])
	          	 $("#cc_term").html(central_sums[3])
	          	 var data=[];

	          	 $.each(rawdata, function(index, row) {
	          	 	data.push({
	          	 		label:row["name"],
	          	 		data: row["termcc"] *100.0 / central_sums[3],
	          	 		color: colors[index%colors.length]
	          	 	}) 
	          	 	
	          	 });
	          	 console.log("dta=="+JSON.stringify(data))
		   //        var data = [
					// { label: "social networks",  data: 38.7, color: "#68BC31"},
					// { label: "search engines",  data: 24.5, color: "#2091CF"},
					// { label: "ad campaigns",  data: 8.2, color: "#AF4E96"},
					// { label: "direct traffic",  data: 18.6, color: "#DA5430"},
					// { label: "other",  data: 10, color: "#FEE074"}
				 //  ]
				  function drawPieChart(placeholder, data, position) {
				 	  $.plot(placeholder, data, {
						series: {
							pie: {
								show: true,
								tilt:0.8,
								highlight: {
									opacity: 0.25
								},
								stroke: {
									color: '#fff',
									width: 2
								},
								startAngle: 2
							}
						},
						legend: {
							show: true,
							position: position || "ne", 
							labelBoxBorderColor: null,
							margin:[-30,15]
						}
						,
						grid: {
							hoverable: true,
							clickable: true
						}
					 })
				 }
				 drawPieChart(placeholder, data);
				
				 /**
				 we saved the drawing function and the data to redraw with different position later when switching to RTL mode dynamically
				 so that's not needed actually.
				 */
				 placeholder.data('chart', data);
				 placeholder.data('draw', drawPieChart);
				
				
				  //pie chart tooltip example
				  var $tooltip = $("<div class='tooltip top in'><div class='tooltip-inner'></div></div>").hide().appendTo('body');
				  var previousPoint = null;
				
				  placeholder.on('plothover', function (event, pos, item) {
					if(item) {
						if (previousPoint != item.seriesIndex) {
							previousPoint = item.seriesIndex;
							var tip = item.series['label'] + " : " + item.series['percent']+'%';
							$tooltip.show().children(0).text(tip);
						}
						$tooltip.css({top:pos.pageY + 10, left:pos.pageX + 10});
					} else {
						$tooltip.hide();
						previousPoint = null;
					}
					
				 });
	          })
	          .fail(function(err,data) {
	          	console.log("error:"+JSON.stringify(err)+","+data);
	          })
	          ;
	          
			 

	        },
	    	

	 } //---xwatable-return;
}();
