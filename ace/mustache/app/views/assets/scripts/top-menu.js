jQuery(function($) {
 var $sidebar = $('.sidebar').eq(0);
 if( !$sidebar.hasClass('h-sidebar') ) return;

 $(document).on('settings.ace.top_menu' , function(ev, event_name, fixed) {
	if( event_name !== 'sidebar_fixed' ) return;

	var sidebar = $sidebar.get(0);
	var $window = $(window);

	//return if sidebar is not fixed or in mobile view mode
	if( !fixed || ( ace.helper.mobile_view() || ace.helper.collapsible() ) ) {
		$sidebar.removeClass('hide-before');
		//restore original, default marginTop
		ace.helper.removeStyle(sidebar , 'margin-top')

		$window.off('scroll.ace.top_menu')
		return;
	}


	 var done = false;
	 $window.on('scroll.ace.top_menu', function(e) {

		var scroll = $window.scrollTop();
		scroll = parseInt(scroll / 4);//move the menu up 1px for every 4px of document scrolling
		if (scroll > 17) scroll = 17;


		if (scroll > 16) {			
			if(!done) {
				$sidebar.addClass('hide-before');
				done = true;
			}
		}
		else {
			if(done) {
				$sidebar.removeClass('hide-before');
				done = false;
			}
		}

		sidebar.style['marginTop'] = (17-scroll)+'px';
	 }).triggerHandler('scroll.ace.top_menu');

 }).triggerHandler('settings.ace.top_menu', ['sidebar_fixed' , $sidebar.hasClass('sidebar-fixed')]);

 $(window).on('resize.ace.top_menu', function() {
	$(document).triggerHandler('settings.ace.top_menu', ['sidebar_fixed' , $sidebar.hasClass('sidebar-fixed')]);
 });


});
