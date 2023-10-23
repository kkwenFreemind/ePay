/* ======================================================================================== 
	A+ 亞太電信官方網站 2014(c)
======================================================================================== */
/******************************************* Back to Top */
jQuery(document).ready(function($) {

	$("body").append("<a href='#top' class='backtopbutton'><i class='fa fa-chevron-up'></i></a>");
	var topBtn = $('.backtopbutton');
	topBtn.hide();
	$(window).scroll(function () {
		if ($(this).scrollTop() > 160) {
			topBtn.fadeIn();
		} else {
			topBtn.fadeOut();
		}
	});
	topBtn.click(function() {
		$("html, body").animate({ scrollTop: 0 }, "slow");
		return false;
	});

});

// on ready event
$(document).ready(function() {
	//	alert(iphone/i.test(navigator.userAgent.toLowerCase()));
	apt_unit();
});

// on resize event
$(window).resize(function() {});

// Category menu scroll auto hide
$(window).scroll(function() {
	sitemap_scroll();
});

//sitemap menu
function apt_unit() {
	// Sitemaps Button
	if ($(".btn-group").hasClass("apt-menu-sitemap")) {
		$(".sitemap-header a").click(function(event) {
			if ($(this).parent().hasClass("btn-close-sitemap") == false) {
				event.stopPropagation();
			}
		});
	}

	// Mobile Button
	if ($(".apt-mbi-btn a").hasClass("btn-mbi-left")) {
		$(".btn-mbi-left").click(function(event) {
			$("body").toggleClass("left-open");
			$("body").removeClass("right-open");
		});
		$(".mbi-mask").click(function(event) {
			$("body").removeClass("left-open");
		});
	}
	if ($(".apt-mbi-btn a").hasClass("btn-mbi-right")) {
		$(".btn-mbi-right").click(function(event) {
			$("body").toggleClass("right-open");
			$("body").removeClass("left-open");
		});
		$(".mbi-mask").click(function(event) {
			$("body").removeClass("right-open");
		});
	}
	mini_nav();
	//sitemap_format();
	modal_iframe();
}

function mini_nav() {
	$('.apt-main-nav>.nav a').onclick = function() {
		$('this').attr("href", "#this")
		alert($("this").attr("href"));
		//		$('this').attr("href", "#this")
		//		document.getElementById("abc").href="xyz.php";
	};
}

function sitemap_format() {
	$('.apt-menu-sitemap').addClass('open');
	var _sm = $('.apt-menu-sitemap .nav>.dropdown-submenu>ul');
	var _txt = "";
	for (var i = 0; i < _sm.length; i++) {
		var _li2 = _sm.eq(i).find(">li");
		var _li2_maxhHeight = 0;
		var _li2_arry = [];
		for (var j = 0; j < _li2.length; j++) {
			_li2_arry.push(_li2.eq(j));
			if (_li2.eq(j).height() > _li2_maxhHeight) {
				_li2_maxhHeight = _li2.eq(j).height();
			}
			if (((j + 1) % 5 == 0) || ((j + 1) == _li2.length)) {
				for (var k = 0; k < _li2_arry.length; k++) {
					_li2_arry[k].height(_li2_maxhHeight);
				}
				_txt += ("  [" + String((j + 1) % 5) + "> (" + _li2_arry.length + ") *** [" + _li2_arry + "] --- " + _li2_maxhHeight + "]  ");
				_li2_maxhHeight = 0;
				_li2_arry.length = 0;
			}
		}
	}
	$('.apt-menu-sitemap').removeClass('open');
}
// sitemap scroll
function sitemap_scroll() {
	if ($('.apt-menu-sitemap').hasClass('open')) {
		$('.apt-menu-sitemap').removeClass('open');
	}
}
// Modal iframe
function modal_iframe(){
	if($('a[data-target="#iframeModal"]').length>0){
		$('a[data-target="#iframeModal"]').on('click', function(e) {
			e.preventDefault();
			var url = $(this).attr('href');
			$("#iframeModal .modal-body").html('<iframe width="100%" height="100%" frameborder="0" scrolling="yes" allowtransparency="true" src="'+url+'"></iframe>');
		});
		$('body').append('<div class="modal iframe-modal fade" id="iframeModal" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true"><div class="modal-dialog"><div class="modal-content"><div class="modal-body"></div></div></div></div>');
	}
}