(function(exports){

    /**
     * @description create json based on query params
     *
     * @param url - url to be parsed
     */
    exports.parse = function(url){
        if(!url)
            return {};
        var index = url.indexOf('?');
        if(index > -1){
            var result = {};
            var query = url.substr(index + 1);

            query.split('&').forEach(function(param){
                var array = param.split('=');

                result[array[0]] = array[1];
            });

            return result;
        }
        return {};
    };

    /**
     *
     * @param obj {Object} - object to be converted to json and encoded
     * @return {String} encoded string
     */
    exports.encode = function(obj) {
        var clone = {};
        for (var key in obj) {
            if (obj.hasOwnProperty(key)) {
                var value = valid(obj[key]);
                if (value) {
                    clone[key] = value;
                }
            }
        }
        return encodeURI(JSON.stringify(clone));
    };

    function valid(val){
        if(!val)
            return null;
        return val;
    }

})(window.UrlUtil = {});