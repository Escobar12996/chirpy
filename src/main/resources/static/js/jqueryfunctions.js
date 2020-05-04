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

$(".deletebutton").click(function(){
    let value = $(this).val();

    $.ajax({
        type: "POST",
        url: "/deletepost/"+value,
        beforeSend: function(xhr) {
            xhr.setRequestHeader(header, token);
        },
        success: function (response) {
            $("#post"+value).hide();
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

$("#menu-toggle").click(function(e) {
    e.preventDefault();
    $("#wrapper").toggleClass("toggled");
  });


$(function() {
    if (document.URL.split(window.location.host)[1] === "/explorer"){
        loadFollow();
    }
});

  
$(document).on('click', '[data-toggle="lightbox"]', function(event) {
  event.preventDefault();
  $(this).ekkoLightbox();
});

$(".r-button").click(function(e) {
    
    let res = $(this).attr("value");
    e.preventDefault();
    setCookie("resp", res, 1);
});

function setCookie(cname, cvalue, hours) {
   var d = new Date();
   d.setTime(d.getTime() + (hours*60*60*1000));
   var expires = "expires="+ d.toUTCString();
   document.cookie = cname + "=" + cvalue + ";" + expires + ";path=/";
}


$("#sendPublication").submit(function(event){
	event.preventDefault();
	var formData = new FormData(this);
	
	$.ajax({
		url : "/home/response",
		type: "POST",
		data : formData,
		processData: false,
	    contentType: false,
		beforeSend: function(xhr) {
            xhr.setRequestHeader(header, token);
        },
        success: function(response){
        	
        	$("#errorview").html(response);
        }});
        return false;
});

$(".div-buttom").click(function() {
	  window.location = "/viewpublication/"+$(this).attr('id'); 
	  return false;
});

(function($) {

	"use strict";

	var fullHeight = function() {

		$('.js-fullheight').css('height', $(window).height());
		$(window).resize(function(){
			$('.js-fullheight').css('height', $(window).height());
		});

	};
	fullHeight();

	$('#sidebarCollapse').on('click', function () {
      $('#sidebar').toggleClass('active');
  });

})(jQuery);

















