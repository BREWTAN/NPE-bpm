var Duster = function(){

	return {
		buildArr:function(tmplEle){
			var buildingTemplate = dust.compile(tmplEle.html(),"_dust_"+tmplEle.id);
      dust.loadSource(buildingTemplate);
      return function(building) { 
          var result;   
          dust.render("_dust_"+tmplEle.id, {"data":building}, function(err, res) {
            result = res;
        });   
        return result;
      };
		}


	};//return

}();