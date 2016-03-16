var urlRestAPIFrontPart = "http://127.0.0.1:88/oi/chat/";

function getQueryStringByName(name){
    var result = location.search.match(new RegExp("[\?\&]" + name+ "=([^\&]+)","i"));
    if(result == null || result.length < 1){
        return "";
    }
    return decodeURI(result[1]);
}

function writeObj(obj){
	var description = "";
	for(var i in obj){ 
		var property=obj[i]; 
		description+=i+" = "+property+"\n";
	} 
	alert(description);
}


