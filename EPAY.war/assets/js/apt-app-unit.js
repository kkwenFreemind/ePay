

/*! matchMedia() polyfill addListener/removeListener extension. Author & copyright (c) 2012: Scott Jehl. Dual MIT/BSD license */
(function(){
    // Bail out for browsers that have addListener support
    if (window.matchMedia && window.matchMedia('all').addListener) {
        return false;
    }

    var localMatchMedia = window.matchMedia,
        hasMediaQueries = localMatchMedia('only all').matches,
        isListening     = false,
        timeoutID       = 0,    // setTimeout for debouncing 'handleChange'
        queries         = [],   // Contains each 'mql' and associated 'listeners' if 'addListener' is used
        handleChange    = function(evt) {
            // Debounce
            clearTimeout(timeoutID);

            timeoutID = setTimeout(function() {
                for (var i = 0, il = queries.length; i < il; i++) {
                    var mql         = queries[i].mql,
                        listeners   = queries[i].listeners || [],
                        matches     = localMatchMedia(mql.media).matches;

                    // Update mql.matches value and call listeners
                    // Fire listeners only if transitioning to or from matched state
                    if (matches !== mql.matches) {
                        mql.matches = matches;

                        for (var j = 0, jl = listeners.length; j < jl; j++) {
                            listeners[j].call(window, mql);
                        }
                    }
                }
            }, 30);
        };

    window.matchMedia = function(media) {
        var mql         = localMatchMedia(media),
            listeners   = [],
            index       = 0;

        mql.addListener = function(listener) {
            // Changes would not occur to css media type so return now (Affects IE <= 8)
            if (!hasMediaQueries) {
                return;
            }

            // Set up 'resize' listener for browsers that support CSS3 media queries (Not for IE <= 8)
            // There should only ever be 1 resize listener running for performance
            if (!isListening) {
                isListening = true;
                window.addEventListener('resize', handleChange, true);
            }

            // Push object only if it has not been pushed already
            if (index === 0) {
                index = queries.push({
                    mql         : mql,
                    listeners   : listeners
                });
            }

            listeners.push(listener);
        };

        mql.removeListener = function(listener) {
            for (var i = 0, il = listeners.length; i < il; i++){
                if (listeners[i] === listener){
                    listeners.splice(i, 1);
                }
            }
        };

        return mql;
    };
}());




/* ======================================================================================== 
	A+ 亞太電信官方網站 2014(c)
======================================================================================== */
// on ready event
$(document).ready(function() {
	// top button
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
	//	alert(iphone/i.test(navigator.userAgent.toLowerCase()));
	apt_unit();
	//mini_nav();

	//dropdown do not closing when clicked
    $('.dropdown-menu input, .dropdown-menu label').click(function(e) {
        e.stopPropagation();
    });
});

// on resize event
$(window).resize(function() {
	//mini_nav();
	//$("body").removeClass("right-open");
	//$("body").removeClass("left-open");
	sitemap_crazy ();
});

// Category menu scroll auto hide
$(window).scroll(function() {
	sitemap_scroll();
});

//sitemap menu
function apt_unit() {
	// Sitemaps Button
	if ($(".btn-group").hasClass("apt-menu-sitemap")) {
		$(".apt-menu-sitemap > a").click(function(event) {
			var _body_t = $('body').scrollTop() ;
			var _header_h = $('header').height();
			var _crazy = $('.apt-top-alert').height();
			//alert( '_body_t:' + _body_t + '_header_h:' + _header_h + ' == _crazy: '+ _crazy );
			//$('body').prepend('<div style="position: fixed; z-index: 999999; background-color: #ccc;">_body_t:' + _body_t + '<br />_header_h:' + _header_h + '<br />_crazy: '+ _crazy + '</div>')
			if($('header').hasClass("affix")){
				var _menu_top = _header_h;
			} else {
				var _menu_top = _header_h + _crazy - _body_t;
			}
			
			$(".apt-menu-sitemap > .dropdown-menu.pull-right").css('top', _menu_top-2 );
		});
		$(".apt-menu-sitemap > a").on('touchstart', function(e) {
			var _body_t = $('body').scrollTop() ;
			var _header_h = $('header').height();
			var _crazy = $('.apt-top-alert').height();
			//$('body').prepend('<div style="position: fixed; z-index: 999999; background-color: #ccc;">_body_t:' + _body_t + '<br />_header_h:' + _header_h + '<br />_crazy: '+ _crazy + '</div>')
			if($('header').hasClass("affix")){
				var _menu_top = _header_h;
			} else {
				var _menu_top = _header_h + _crazy - _body_t;
			}
			
			$(".apt-menu-sitemap > .dropdown-menu.pull-right").css('top', _menu_top-2 );
		});
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
	sitemap_format();
	sitemap_crazy ();
	modal_iframe();
	mini_nav();
}
//
function mini_nav() {
	//alert(window.matchMedia('(max-width: 767px)').matches);
	if(window.matchMedia('(max-width: 767px)').matches){
		//alert($('.apt-main-nav>.nav li.dropdown-submenu').length)
		var _trg = $('.apt-main-nav>.nav li.dropdown-submenu');
		$('.apt-main-nav>.nav li.active').addClass("open");
		_trg.addClass("dropdown-submenu-mini");
		_trg.removeClass("dropdown-submenu");
		var _trgdm = $('.apt-main-nav>.nav li.dropdown-submenu-mini>a');
		for (var i = 0; i < _trgdm.length; i++) {
			var _href = _trgdm.eq(i);
			var _url = _href.attr('href');
			if(_url=="#"){
				_trgdm.addClass("dropdown-btn");
				_href.parent().append( "<a class='nav-link-btn' href='#this'><i class='fa-plus'></i></a>" );
			}else{
				_href.attr('data-url',_url);
				_href.parent().append( "<a class='nav-link-btn' href='#this'><i class='fa-plus'></i></a>" );
				//_href.attr('href','#this');
			}
		}
		//_trgdm.bind( "click", function() {
		$('.nav-link-btn').bind( "click", function() {
			$(this).parent().toggleClass("open");
		});
	}else{
		var _trg = $('.apt-main-nav>.nav li.dropdown-submenu-mini');
		$('.apt-main-nav>.nav li').removeClass("open");
		_trg.addClass("dropdown-submenu");
		_trg.removeClass("dropdown-submenu-mini");
		$( ".apt-main-nav .nav-link-btn" ).remove();
		var _trgdm = $('.apt-main-nav>.nav li.dropdown-submenu-mini>a');
		//_trgdm.unbind.unbind( "click", function() { });
		//
	}
}
//
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
//
function sitemap_crazy () {
	if(window.matchMedia('(max-width: 767px)').matches){
		if($('.alert').hasClass('apt-top-alert')){
			$('body').addClass("apt-top-ad");
			$( "header" ).attr( 'data-offset-top' , '60');
			$('header').affix();
			$(".apt-top-alert .close").click(function(event) {
				$('body').removeClass("apt-top-ad");
			});
		}
	} else {
		$('body').removeClass("apt-top-ad");
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