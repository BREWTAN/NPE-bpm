var Utils  = function () {

    return {
        //main function to initiate the module
        queryURL:function(url){
            // console.log("url=="+url+"::match="+url.match(/.*\?.*/));
            if(url.match(/.*\?.*/)){
                if(url.match(/\&$/)||url.match(/\?$/)){
                    return url;
                }
                else return url+"&";
            }
            return url+"?";
        },//--queryURL

    };

}();

