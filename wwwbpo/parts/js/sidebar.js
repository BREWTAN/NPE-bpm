var SideBar = function() {
    return {
        init: function() {
            console.log("init")
        }, //--End ofinit
        activeCurByPage: function(){

            var pages=window.location.pathname.split('/');
            var pageName=pages[pages.length-1];
            console.log("pages::"+pageName);

            if(pageName==""||pageName=="index.html"){ 
                var pp= $("#__sidebar>li.start");
                pp.addClass('active');
                pp.children('a').children('span.arrow').removeClass('arrow').addClass('selected')
                return;
            }
            $(".page-sidebar-menu .active").removeClass("active");
            var links = $("#__sidebar").find("a[href$='/"+pageName+"']");
            if(links){
                links.parent("li").addClass("active");
                var pp=links.parentsUntil('sub-menu').parent('li');
                pp.addClass('active')
                
            }

        },
    };
}();

var template=Duster.buildArr($("#dust_sidebar"));

// var data = Restful.find("/lbosdb/menu","{\"rolecode\":\""+userInfo.rolecode+"\"}");// http://localhost:8080/lbosdbNew/menu
//http://192.168.100.28:8080/lbos/menu?query={%22username%22:%22admin%22}
//var data = Restful.findNQ("/lbosdb/menu?query={\"username\":\""+loginname+"\"}");

var jsonData;
//var restURL = "/lbosdb/menu?query={\"username\":\""+loginname+"\"}";
$.ajax({
    type: 'get',
    url: "rest/sidebar.json",
    dataType: 'json',
    contentType: "application/json; charset=utf-8",
    async: false,
    success: function(data, textStatus, xhr) {
        console.log(xhr.status);
        if(xhr.status == 200){
            jsonData = data;
        } else if(xhr.status == 403){
            errorProcess();
        }
    },
    error: function(err) {
        if(err.status == 403){
            errorProcess();
        }
    }
});

function errorProcess(){
    console.log("403错误");
    $('#checkbasic').modal('show');
}

$("#__sidebar").append(template(jsonData));
SideBar.init();
SideBar.activeCurByPage();


