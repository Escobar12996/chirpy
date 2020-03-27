    var token = $("meta[name='_csrf']").attr("content");
    var header = $("meta[name='_csrf_header']").attr("content");

$(".followbutton").click(function(){
    let value = $(this).val();
    
    $.ajax({
        type: "POST",
        url: "/follow/"+value,
        beforeSend: function(xhr) {
            xhr.setRequestHeader(header, token);
        },

        success: function (response) {
            $("#followbutton"+value).hide();
            $("#unfollowbutton"+value).show();
        }
    });
});

$(".unfollowbutton").click(function(){
    let value = $(this).val();

    $.ajax({
        type: "POST",
        url: "/unfollow/"+value,
        beforeSend: function(xhr) {
            xhr.setRequestHeader(header, token);
        },
        success: function (response) {
            $("#followbutton"+value).show();
            $("#unfollowbutton"+value).hide();
        }
    });
});

function loadFollow(){
        $.ajax({
            type: "POST",
            url: "/getfollows",
            beforeSend: function(xhr) {
                xhr.setRequestHeader(header, token);
            },
            success: function(response){
                $.each(response,function(indice,objeto){
                    $("#followbutton"+objeto.id).hide();
                    $("#unfollowbutton"+objeto.id).show();
                });
            }
        });
}



$(function() {
    if (document.URL.split(window.location.host)[1] === "/explorer"){
        loadFollow();
    }
});



