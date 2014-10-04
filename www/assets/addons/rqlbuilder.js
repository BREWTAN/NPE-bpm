var RQLBuilder = function()
{
    return {
        like:function(cname,cvalue){
            return ('"'+cname+'":{"$regex":"'+cvalue+'","$options":"i"}')
        },
        equal:function(cname,cvalue){
            return('"'+cname+'":"'+cvalue+'"')
        },
        condition:function(cname,con,cvalue){
            //!!<, <=, >, >= and !=
            // $lt, $lte, $gt, $gte and $ne
            return('"'+cname+'":{"'+con+'":"'+cvalue+'"}')
        },
        and:function(rqls){
            var ret="";
            $.each(rqls, function(index, val) {
                if(val&&val.length>0)
                { 
                    if(ret.length>0){
                        ret+=","
                    }
                    ret+=val;
                }
            });
            return ret;
        },
        or:function(rqls){
            var ret='"$or":['
            var i=0;
            $.each(rqls, function(index, val) {
                if(i>0)ret+=",";
                i++;
                 ret+="{"+val+"}"
            });
            ret+="]";
            return ret;
        }
       
    };//----rqlbuilder.return
}();///-----RQLBuilder

String.prototype.rql = function(){
    return encodeURIComponent('{'+this+'}');
}


