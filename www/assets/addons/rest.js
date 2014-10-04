var Restful = function() {

    return {
        // GET /db/collection - Returns all documents
        // GET /db/collection?query=%7B%22isDone%22%3A%20false%7D - Returns all documents satisfying query
        // GET /db/collection?query=%7B%22isDone%22%3A%20false%7D&limit=2&skip=2 - Ability to add options to query (limit, skip, etc)
        // GET /db/collection/id - Returns document with id
        // POST /db/collection - Insert new document in collection (document in POST body)
        // PUT /db/collection/id - Update document with id (updated document in PUT body)
        // DELETE /db//collectionid - Delete document with id
        find:function(restURL, queryJsonStr) {
            var jsonData;
            console.log("::restful.query::" + queryJsonStr);
            $.ajax({
                type: 'get',
                url: restURL + "/?query=" + encodeURIComponent(queryJsonStr),
                dataType: 'json',
                contentType: "application/json; charset=utf-8",
                async: false,
                success: function(data) {
                    jsonData = data;
                },
                error: function(err) {
                    // alert("find all err");
                }
            });
            return jsonData;
        },

        findNQ:function(restURL) {
            var jsonData;
            $.ajax({
                type: 'get',
                url: restURL,
                dataType: 'json',
                contentType: "application/json; charset=utf-8",
                async: false,
                success: function(data) {
                    jsonData = data;
                },
                error: function(err) {
                    // alert("find all err");
                }
            });
            return jsonData;
        },

        findPNQ:function(restURL, queryJsonStr) {
            var jsonData;
            $.ajax({
                type: 'POST',
                url: restURL,
                dataType: 'json',
                contentType: "application/json; charset=utf-8",
                async: false,
                data: queryJsonStr,
                success: function(data) {
                    jsonData = data;
                },
                error: function(err) {
                    // alert("find all err");
                }
            });
            return jsonData;
        },

        rqlLike:function(colname,colvalue){
            rql='{"'+colname+'":{"$regex":"'+colvalue+'","$options":"i"}}';
            return rql;
        },

        findLike:function(restURL,colname,colvalue){
            return find(restURL,rqlLike(colname, colvalue));
        },

        getByID:function(restURL, id) {
            var jsonData;
            $.ajax({
                type: 'get',
                url: restURL + "/" + id,
                dataType: 'json',
                contentType: "application/json; charset=utf-8",
                async: false,
                success: function(data) {
                    jsonData = data;
                },
                error: function(err) {
                    // alert("find all err");
                }
            });
            return jsonData;
        },


        findByPage:function(restURL, queryJsonStr, beginRow, rowNum) {
            var jsonData;
            $.ajax({
                type: 'get',
                url: restURL + "/?query=" + encodeURIComponent(queryJsonStr) + "&skip=" + beginRow + "&limit=" + rowNum,
                dataType: 'json',
                contentType: "application/json; charset=utf-8",
                async: false,
                success: function(data) {
                    jsonData = data;
                },
                error: function(err) {
                    // alert("find all err");
                }
            });
            return jsonData;
        },

        insert:function(restURL, json) {
            var bool;
            $.ajax({
                type: 'POST',
                url:  restURL,
                dataType: 'json',
                contentType: "application/json; charset=utf-8",
                async: false,
                data: JSON.stringify(json),

                success: function(data) {
                    bool = data;
                },
                error: function(err) {}
            });
            return bool;
        },

        insert_batch:function(restURL, arr) {
            var successNum = 0;
            var errorNum = 0;
            for (var i = 0; i < arr.length; i++) {
                $.ajax({
                    type: 'POST',
                    url: restURL,
                    dataType: 'json',
                    contentType: "application/json; charset=utf-8",
                    data: JSON.stringify(arr[i]),
                    async: false,
                    success: function(data) {
                        successNum = successNum + 1;
                    },
                    error: function(err) {
                        errorNum = errorNum + 1;
                    }
                });
            }
            if (errorNum > 0) {}
            return successNum;
        },

        update:function(restURL, id, json) {
            var bool = false;
            $.ajax({
                url: restURL + "/" + id+'/',
                type: 'PUT',
                dataType: 'json',
                contentType: "application/json; charset=utf-8",
                data: JSON.stringify(json),

                async: false,
                // data: json,
                success: function(data) {
                    bool = true;
                },
                error: function(err) {}
            });
            return bool;
        },

        updateNQ:function(restURL, json) {
            var bool = false;
            $.ajax({
                url: restURL,
                type: 'PUT',
                dataType: 'json',
                contentType: "application/json; charset=utf-8",
                data: json,
                async: false,
                
                success: function(data) {
                    bool = true;
                },
                error: function(err) {}
            });
            return bool;
        },

        updateRNQ:function(restURL, json) {
            var resdata = false;
            $.ajax({
                url: restURL,
                type: 'PUT',
                dataType: 'json',
                contentType: "application/json; charset=utf-8",
                data: json,
                async: false,
                
                success: function(data) {
                    resdata = data;
                },
                error: function(err) {}
            });
            return resdata;
        },

        delByID:function(restURL, id) {
            var bool = false;
            $.ajax({
                type: 'DELETE',
                url: restURL + "/" + id,
                dataType: 'json',
                contentType: "application/json; charset=utf-8",
                async: false,
                success: function(data) {
                    bool = true;
                },
                error: function(err) {}
            });
            return bool;
        },

        delByIDR:function(restURL, id) {
            var resdata = false;
            $.ajax({
                type: 'DELETE',
                url: restURL + "/" + id,
                dataType: 'json',
                contentType: "application/json; charset=utf-8",
                async: false,
                success: function(data) {
                    resdata = data;
                },
                error: function(err) {}
            });
            return resdata;
        },

        delByIDS:function(restURL, ids) {
            var bool = false;
            // $.ajax({
            //     type: 'DELETE',
            //     url: restURL + "/" + ids,
            //     dataType: 'json',
            //     async: false,
            //     success: function(data) {
            //         bool = true;
            //     },
            //     error: function(err) {}
            // });
            $.each(ids, function(index, id) {
                $.ajax({
                type: 'DELETE',
                url: restURL + "/" + id,
                dataType: 'json',
                async: false,
                success: function(data) {
                    bool = true;
                },
                error: function(err) {}
            });
            });

            console.log("JSON::"+JSON.stringify(ids))
            // $.ajax({
            //      type: 'POST',
            //      url: restURL + "/batch/delete",
            //      contentType: "application/json; charset=utf-8",
            //      dataType: 'json',

            //      data: JSON.stringify(ids),
            //      async: false,
            //      success: function(data) {
            //          bool = true;
            //      },
            //      error: function(err) {}
            //  });

            return bool;
        },

        count : function(restURL,queryJsonStr){
            var count = 0;
            $.ajax({
                type: 'get',
                url: restURL+"/?query="+encodeURIComponent(queryJsonStr),
                dataType: 'json',
                contentType: "application/json; charset=utf-8",
                async:false,
                success: function(data){
                    count=data.length;
                },
                error: function(err) {
                    // alert("find all err");
                }
            });
            return count;
        },

        postData: function(restURL,jsonData){
            var bool = false;
            $.ajax({
                type: 'POST',
                url:  restURL,
                dataType: 'json',
                contentType: "application/json; charset=utf-8",
                data: jsonData,
                async: false,
                success: function(data) {
                    bool = true;
                },
                error: function(err) {}
            });
            return bool;
        },

         postDataR: function(restURL,jsonData){
            var resdata;
            $.ajax({
                type: 'POST',
                url:  restURL,
                dataType: 'json',
                contentType: "application/json; charset=utf-8",
                data: jsonData,
                async: false,
                success: function(data) {
                    resdata = data;
                },
                error: function(err) {}
            });
            return resdata;
        },

        str2json:function(jsonStr) {
            return eval("(" + jsonStr + ")");
        }
    };
}();///-----rest

