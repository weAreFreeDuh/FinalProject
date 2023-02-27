;(function () {
	
	'use strict';



	var isMobile = {
		Android: function() {
			return navigator.userAgent.match(/Android/i);
		},
			BlackBerry: function() {
			return navigator.userAgent.match(/BlackBerry/i);
		},
			iOS: function() {
			return navigator.userAgent.match(/iPhone|iPad|iPod/i);
		},
			Opera: function() {
			return navigator.userAgent.match(/Opera Mini/i);
		},
			Windows: function() {
			return navigator.userAgent.match(/IEMobile/i);
		},
			any: function() {
			return (isMobile.Android() || isMobile.BlackBerry() || isMobile.iOS() || isMobile.Opera() || isMobile.Windows());
		}
	};

	var fullHeight = function() {

		if ( !isMobile.any() ) {
			$('.js-fullheight').css('height', $(window).height());
			$(window).resize(function(){
				$('.js-fullheight').css('height', $(window).height());
			});
		}

	};


	var counter = function() {
		$('.js-counter').countTo({
			 formatter: function (value, options) {
	      return value.toFixed(options.decimals);
	    },
		});
	};


	var counterWayPoint = function() {
		if ($('#colorlib-counter').length > 0 ) {
			$('#colorlib-counter').waypoint( function( direction ) {
										
				if( direction === 'down' && !$(this.element).hasClass('animated') ) {
					setTimeout( counter , 400);					
					$(this.element).addClass('animated');
				}
			} , { offset: '90%' } );
		}
	};

	// Animations
	var contentWayPoint = function() {
		var i = 0;
		$('.animate-box').waypoint( function( direction ) {

			if( direction === 'down' && !$(this.element).hasClass('animated') ) {
				
				i++;

				$(this.element).addClass('item-animate');
				setTimeout(function(){

					$('body .animate-box.item-animate').each(function(k){
						var el = $(this);
						setTimeout( function () {
							var effect = el.data('animate-effect');
							if ( effect === 'fadeIn') {
								el.addClass('fadeIn animated');
							} else if ( effect === 'fadeInLeft') {
								el.addClass('fadeInLeft animated');
							} else if ( effect === 'fadeInRight') {
								el.addClass('fadeInRight animated');
							} else {
								el.addClass('fadeInUp animated');
							}

							el.removeClass('item-animate');
						},  k * 200, 'easeInOutExpo' );
					});
					
				}, 100);
				
			}

		} , { offset: '85%' } );
	};


	var burgerMenu = function() {

		$('.js-colorlib-nav-toggle').on('click', function(event){
			event.preventDefault();
			var $this = $(this);

			if ($('body').hasClass('offcanvas')) {
				$this.removeClass('active');
				$('body').removeClass('offcanvas');	
			} else {
				$this.addClass('active');
				$('body').addClass('offcanvas');	
			}
		});



	};

	// Click outside of offcanvass
	var mobileMenuOutsideClick = function() {

		$(document).click(function (e) {
	    var container = $("#colorlib-aside, .js-colorlib-nav-toggle");
	    if (!container.is(e.target) && container.has(e.target).length === 0) {

	    	if ( $('body').hasClass('offcanvas') ) {

    			$('body').removeClass('offcanvas');
    			$('.js-colorlib-nav-toggle').removeClass('active');
			
	    	}
	    	
	    }
		});

		$(window).scroll(function(){
			if ( $('body').hasClass('offcanvas') ) {

    			$('body').removeClass('offcanvas');
    			$('.js-colorlib-nav-toggle').removeClass('active');
			
	    	}
		});

	};

	var sliderMain = function() {
		
	  	$('#colorlib-hero .flexslider').flexslider({
			animation: "fade",
			slideshowSpeed: 5000,
			directionNav: true,
			start: function(){
				setTimeout(function(){
					$('.slider-text').removeClass('animated fadeInUp');
					$('.flex-active-slide').find('.slider-text').addClass('animated fadeInUp');
				}, 500);
			},
			before: function(){
				setTimeout(function(){
					$('.slider-text').removeClass('animated fadeInUp');
					$('.flex-active-slide').find('.slider-text').addClass('animated fadeInUp');
				}, 500);
			}

	  	});

	};

	var stickyFunction = function() {

		var h = $('.image-content').outerHeight();

		if ($(window).width() <= 992 ) {
			$("#sticky_item").trigger("sticky_kit:detach");
		} else {
			$('.sticky-parent').removeClass('stick-detach');
			$("#sticky_item").trigger("sticky_kit:detach");
			$("#sticky_item").trigger("sticky_kit:unstick");
		}

		$(window).resize(function(){
			var h = $('.image-content').outerHeight();
			$('.sticky-parent').css('height', h);


			if ($(window).width() <= 992 ) {
				$("#sticky_item").trigger("sticky_kit:detach");
			} else {
				$('.sticky-parent').removeClass('stick-detach');
				$("#sticky_item").trigger("sticky_kit:detach");
				$("#sticky_item").trigger("sticky_kit:unstick");

				$("#sticky_item").stick_in_parent();
			}
			

			

		});

		$('.sticky-parent').css('height', h);

		$("#sticky_item").stick_in_parent();

	};

	var owlCrouselFeatureSlide = function() {
		$('.owl-carousel').owlCarousel({
			animateOut: 'fadeOut',
		   animateIn: 'fadeIn',
		   autoplay: true,
		   loop:true,
		   margin:0,
		   nav:true,
		   dots: false,
		   autoHeight: true,
		   items: 1,
		   navText: [
		      "<i class='icon-arrow-left3 owl-direction'></i>",
		      "<i class='icon-arrow-right3 owl-direction'></i>"
	     	]
		})
	};

	// Document on load.
	$(function(){
		fullHeight();
		counter();
		counterWayPoint();
		contentWayPoint();
		burgerMenu();
		mobileMenuOutsideClick();
		sliderMain();
		stickyFunction();
		owlCrouselFeatureSlide();
	});


}());


/* ===================================================================
 * TypeRite - Main JS
 *
 * ------------------------------------------------------------------- */

(function($) {

	"use strict";

	var cfg = {
			scrollDuration : 800, // smoothscroll duration
			mailChimpURL   : ''   // mailchimp url
		},

		$WIN = $(window);

	// Add the User Agent to the <html>
	// will be used for IE10/IE11 detection (Mozilla/5.0 (compatible; MSIE 10.0; Windows NT 6.2; Trident/6.0; rv:11.0))
	var doc = document.documentElement;
	doc.setAttribute('data-useragent', navigator.userAgent);


	/* Preloader
     * -------------------------------------------------- */
	var ssPreloader = function() {

		$("html").addClass('ss-preload');

		$WIN.on('load', function() {

			//force page scroll position to top at page refresh
			// $('html, body').animate({ scrollTop: 0 }, 'normal');

			// will first fade out the loading animation
			$("#loader").fadeOut("slow", function() {
				// will fade out the whole DIV that covers the website.
				$("#preloader").delay(300).fadeOut("slow");
			});

			// for hero content animations
			$("html").removeClass('ss-preload');
			$("html").addClass('ss-loaded');

		});
	};


	/* Pretty Print
     * -------------------------------------------------- */
	var ssPrettyPrint = function() {
		$('pre').addClass('prettyprint');
		$( document ).ready(function() {
			prettyPrint();
		});
	};


	/* search
     * ------------------------------------------------------ */
	var ssSearch = function() {

		var searchWrap = $('.header__search'),
			searchField = searchWrap.find('.search-field'),
			closeSearch = searchWrap.find('.header__search-close'),
			searchTrigger = $('.header__search-trigger'),
			siteBody = $('body');


		searchTrigger.on('click', function(e) {

			e.preventDefault();
			e.stopPropagation();

			var $this = $(this);

			siteBody.addClass('search-is-visible');
			setTimeout(function(){
				searchWrap.find('.search-field').focus();
			}, 100);

		});

		closeSearch.on('click', function(e) {

			var $this = $(this);

			e.stopPropagation();

			if(siteBody.hasClass('search-is-visible')){
				siteBody.removeClass('search-is-visible');
				setTimeout(function(){
					searchWrap.find('.search-field').blur();
				}, 100);
			}
		});

		searchWrap.on('click',  function(e) {
			if( !$(e.target).is('.search-field') ) {
				closeSearch.trigger('click');
			}
		});

		searchField.on('click', function(e){
			e.stopPropagation();
		});

		searchField.attr({placeholder: 'Type Keywords', autocomplete: 'off'});

	};


	/* menu
     * ------------------------------------------------------ */
	var ssMenu = function() {

		var menuToggle = $('.header__menu-toggle'),
			siteBody = $('body');

		menuToggle.on('click', function(e) {
			e.preventDefault();
			e.stopPropagation();
			menuToggle.toggleClass('is-clicked');
			siteBody.toggleClass('nav-wrap-is-visible');
		});

		$('.header__nav .has-children').children('a').on('click', function (e) {

			e.preventDefault();

			$(this).toggleClass('sub-menu-is-open')
				.next('ul')
				.slideToggle(200)
				.end()
				.parent('.has-children')
				.siblings('.has-children')
				.children('a')
				.removeClass('sub-menu-is-open')
				.next('ul')
				.slideUp(200);

		});
	};


	/* masonry
     * ---------------------------------------------------- */
	var ssMasonryFolio = function () {

		var containerBricks = $('.masonry');

		containerBricks.masonry({
			itemSelector: '.masonry__brick',
			columnWidth: '.grid-sizer',
			percentPosition: true,
			resize: true
		});

		// layout Masonry after each image loads
		containerBricks.imagesLoaded().progress( function() {
			containerBricks.masonry('layout');
		});

	};

	/* animate bricks
     * ------------------------------------------------------ */
	var ssBricksAnimate = function() {

		var animateEl = $('.animate-this');

		$WIN.on('load', function() {

			setTimeout(function() {
				animateEl.each(function(ctr) {
					var el = $(this);

					setTimeout(function() {
						el.addClass('animated');
					}, ctr * 200);
				});
			}, 300);

		});

		$WIN.on('resize', function() {
			// remove animation classes
			animateEl.removeClass('animate-this animated');
		});

	};


	/* slick slider
     * ------------------------------------------------------ */
	var ssSlickSlider = function() {

		var $gallery = $('.slider__slides').slick({
			arrows: false,
			dots: true,
			infinite: true,
			slidesToShow: 1,
			slidesToScroll: 1,
			adaptiveHeight: true,
			pauseOnFocus: false,
			fade: true,
			cssEase: 'linear'
		});

		$('.slider__slide').on('click', function() {
			$gallery.slick('slickGoTo', parseInt($gallery.slick('slickCurrentSlide'))+1);
		});

	};


	/* smooth scrolling
     * ------------------------------------------------------ */
	var ssSmoothScroll = function() {

		$('.smoothscroll').on('click', function (e) {
			var target = this.hash,
				$target    = $(target);

			e.preventDefault();
			e.stopPropagation();

			$('html, body').stop().animate({
				'scrollTop': $target.offset().top
			}, cfg.scrollDuration, 'swing').promise().done(function () {

				// check if menu is open
				if ($('body').hasClass('menu-is-open')) {
					$('.header-menu-toggle').trigger('click');
				}

				window.location.hash = target;
			});
		});

	};


	/* alert boxes
     * ------------------------------------------------------ */
	var ssAlertBoxes = function() {

		$('.alert-box').on('click', '.alert-box__close', function() {
			$(this).parent().fadeOut(500);
		});

	};


	/* Back to Top
     * ------------------------------------------------------ */
	var ssBackToTop = function() {

		var pxShow      = 500,
			goTopButton = $(".go-top")

		// Show or hide the button
		if ($(window).scrollTop() >= pxShow) goTopButton.addClass('link-is-visible');

		$(window).on('scroll', function() {
			if ($(window).scrollTop() >= pxShow) {
				if(!goTopButton.hasClass('link-is-visible')) goTopButton.addClass('link-is-visible')
			} else {
				goTopButton.removeClass('link-is-visible')
			}
		});
	};


	/* Initialize
     * ------------------------------------------------------ */
	(function clInit() {

		ssPreloader();
		ssPrettyPrint();
		ssSearch();
		ssMenu();
		ssMasonryFolio();
		ssBricksAnimate();
		ssSlickSlider();
		ssSmoothScroll();
		ssAlertBoxes();
		ssBackToTop();

	})();

})(jQuery);