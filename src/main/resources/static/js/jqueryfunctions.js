    var token = $("meta[name='_csrf']").attr("content");
    var header = $("meta[name='_csrf_header']").attr("content");
    var finish = false;
    var users = null;
    var bottomOffset = 200; // the distance (in px) from the page bottom when you want to load more posts
    var finishuser = false;
    var finishpubli = false;
    

$(document).on('click', '.followbutton', function() {
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

$(document).on('click', '.unfollowbutton', function() {
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

$(document).on('click', '.deletebutton', function(){
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

function detectfollows(){
    $.each(users,function(indice,objeto){
            $("#followbutton"+objeto.id).hide();
            $("#unfollowbutton"+objeto.id).show();
        });
}

function loadFollow(){
    $.ajax({
        type: "POST",
        url: "/getfollows",
        beforeSend: function(xhr) {
            xhr.setRequestHeader(header, token);
        },
        success: function(response){
            users = response;
            detectfollows();
        }
    });
}

$("#menu-toggle").click(function(e) {
    e.preventDefault();
    $("#wrapper").toggleClass("toggled");
  });


$(function() {
    if (document.URL.split(window.location.host)[1].includes("/explorer")){
        loadFollow();
    }
    if (document.URL.split(window.location.host)[1].includes("/profilefollowers/")){
        loadFollow();
    }
});

  
$(document).on('click', '[data-toggle="lightbox"]', function(event) {
  event.preventDefault();
  $(this).ekkoLightbox();
});

$(document).on('click', '.r-button', function(e) {
    
    let res = $(this).attr("value");
    e.preventDefault();
    console.log(res);
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
        	if (response == "true"){
                    $('#myModal').modal('toggle');
                    $('#sendPublication')[0].reset();
                    if (document.URL.split(window.location.host)[1].includes("/home")){
                        newreload();
                    }
                    
                } else {
                    $("#errorview").html(response);
                }
                
        }});
        return false;
});

$(document).on('click', '.div-buttom', function() {
	  window.location = "/viewpublication/"+$(this).attr('id'); 
	  return false;
});

$(document).on('click', '.deleteimage', function(){
    let value = $(this).val();
    console.log(value);
    $.ajax({
        type: "POST",
        url: "/deleteimage/"+value,
        beforeSend: function(xhr) {
            xhr.setRequestHeader(header, token);
        },
        success: function (response) {
            $("#post"+value).hide();
        }
    });
});




jQuery(function($){

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
 
	$(window).scroll(function() {
		
		if(document.URL.split(window.location.host)[1].includes("/home") && $(window).scrollTop() + $(window).height() == $(document).height() && finish === false){
			let value = $('.publication').last().attr('id');
			$.ajax({
				type: "POST",
		        url: "/refill/main",
		        data : { find: 0 , last : value },
		        beforeSend: function(xhr) {
		            xhr.setRequestHeader(header, token);
		        },
				success:function(data){
					
					if (data){
						$("#all-publications").append(data);
					} else {
						finish = true;
					}
					
					
				}
			});
		} else if(document.URL.split(window.location.host)[1].includes("/viewpublication/") && $(window).scrollTop() + $(window).height() == $(document).height() && finish === false){
			let findval = $('.prin').attr('id');
			let value = $('.sec').last().attr('id');
			$.ajax({
				type: "POST",
		        url: "/refill/view",
		        data : { find: findval , last : value},
		        beforeSend: function(xhr) {
		            xhr.setRequestHeader(header, token);
		        },
				success:function(data){
					
					if (data){
						$("#all-publications").append(data);
					} else {
						finish = true;
					}
					
					
				}
			});
		} else if(document.URL.split(window.location.host)[1].includes("/profile/") && $(window).scrollTop() + $(window).height() == $(document).height() && finish === false){
			let findval = $('.prin').attr('id');
			let value = $('.sec').last().attr('id');
			$.ajax({
				type: "POST",
		        url: "/refill/profile",
		        data : { find: findval , last : value},
		        beforeSend: function(xhr) {
		            xhr.setRequestHeader(header, token);
		        },
				success:function(data){
					
					if (data){
						$("#all-publications").append(data);
					} else {
						finish = true;
					}
					
					
				}
			});
		} else if(document.URL.split(window.location.host)[1].includes("/quotes") && $(window).scrollTop() + $(window).height() == $(document).height() && finish === false){
			let value = $('.sec').last().attr('id');
			$.ajax({
				type: "POST",
		        url: "/refill/quotes",
		        data : { find: 0 , last : value},
		        beforeSend: function(xhr) {
		            xhr.setRequestHeader(header, token);
		        },
				success:function(data){
					
					if (data){
						$("#all-publications").append(data);
					} else {
						finish = true;
					}
					
					
				}
			});
		} else if(document.URL.split(window.location.host)[1].includes("/profileimages/") && $(window).scrollTop() + $(window).height() == $(document).height() && finish === false){
			let value = $('.sec').last().attr('id');
                        let fi = $('.prin').attr('id');
			$.ajax({
			type: "POST",
		        url: "/refill/refillimages",
		        data : { find: fi , last : value},
		        beforeSend: function(xhr) {
		            xhr.setRequestHeader(header, token);
		        },
				success:function(data){
					
					if (data){
						$("#all-images").append(data);
					} else {
						finish = true;
					}
					
					
				}
			});
		}
	});
});

$(document).on('click', '.new_reload', function(){
    newreload();
});

function newreload(){
    $.ajax({
        type: "POST",
        url: "/refill/reloadnew",
        data : { find: 0 , last : $('.publication').first().attr('id')},
        beforeSend: function(xhr) {
            xhr.setRequestHeader(header, token);
        },
        success:function(data){

            if (data){
                $(".pri").after(data);
            }
        }
    });
}



$(document).on('click', '.refilluser', function(){
    let lasts = $('.user').last().attr('id');

    if (finishuser === false){
            $.ajax({
            type: "POST",
            url: "/refillexplorer",
            data : { last: lasts , finder : "user"},
            beforeSend: function(xhr) {
                xhr.setRequestHeader(header, token);
            },
            success:function(data){

                if (data){
                    $("#users").append(data);
                } else {
                    finishuser = true;
                }
            }
        });
    }
    
});


$(document).on('click', '.refillpublications', function(){
    let lasts = $('.publication').last().attr('id');

    if (finishpubli  === false){
        $.ajax({
            type: "POST",
            url: "/refillexplorer",
            data : { last: lasts , finder : "publication"},
            beforeSend: function(xhr) {
                xhr.setRequestHeader(header, token);
            },
            success:function(data){

                if (data){
                    $("#publications").append(data);
                    console.log(data);
                } else {
                    finishpubli = true;
                }
            }
        });
    }
});