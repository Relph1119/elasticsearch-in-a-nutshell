/*$(document).ready(function(){
    //全局变量
    var searchs=$('#nav-left a');

    //点击按钮跳转事件
    $("#search-button").click(function(){
    	jump();
    });

    //回车跳转
    $("#input").keydown(function(event){
    	if(event.which == "13"){
    		jump();
    	}
    });

    //跳转页面
    function jump(){
        var input = $("#input").val();
        if(input == ''){
            return false;
        }else {
            if(input.length > 50) {
            var input = input.substring(0,50);
            var url = "/search/results?page=1";
            window.location.href = url;//在当前页面刷新
            }else {
                 var url = "/search/results?page=1";
                window.location.href = url;
            }
        }
    }

    //默认情况下让鼠标的焦点在输入框中
    $("#input").focus();

   /* for(var i=0; i<searchs.length;i++) {
        searchs[i].onclick=function() {
            for(var j=0; j<searchs.length;j++) {
                searchs[j].className='';
            }
            this.className='selected';
        };
    }*/
});*/