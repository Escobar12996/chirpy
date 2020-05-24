    var token = $("meta[name='_csrf']").attr("content");
    var header = $("meta[name='_csrf_header']").attr("content");
    var finish = false;
    var bottomOffset = 200; // the distance (in px) from the page bottom when you want to load more posts
    

jQuery(function($){

	
	$(window).scroll(function() {
		
		if(document.URL.split(window.location.host)[1].includes("/administration/userpost") && $(window).scrollTop() + $(window).height() == $(document).height() && finish === false){
			let value = $('.last').last().attr('id');
			let fin = $('.find').last().attr('id');
			$.ajax({
				type: "POST",
		        url: "/refill/adminprof",
		        data : { find: fin , last : value },
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
		} else if(document.URL.split(window.location.host)[1].includes("/administration/posts") && $(window).scrollTop() + $(window).height() == $(document).height() && finish === false){
			let value = $('.last').last().attr('id');
			$.ajax({
				type: "POST",
		        url: "/refill/allpublicationsadmin",
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
		} else if(document.URL.split(window.location.host)[1].includes("/administration/userimages/") && $(window).scrollTop() + $(window).height() == $(document).height() && finish === false){
			let value = $('.last').last().attr('id');
			let fin = $('.find').last().attr('id');
                        
			$.ajax({
				type: "POST",
		        url: "/refill/adminfimapro",
		        data : { find: fin , last : value },
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
