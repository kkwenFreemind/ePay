// JavaScript Document
!function ($) {
$(document).ready(function () {
	function accordionOpen(_id,_no){
		$(_id).find('.accordion-body').eq([_no]).addClass("in")
		$(_id).find('.accordion-body').eq([_no]).parent().addClass("open")
		$(_id).find('.accordion-toggle').on('click', function(event){
			_group = $(this).parent().parent().parent().find('.accordion-group').removeClass("open")
			var _group = $(this).parent().parent();
			_group.addClass("open")
		});
	}
	//accordionOpen(id 名稱,指定開啟選單號碼)
	accordionOpen("#collapse-menu",10);
	accordionOpen("#collapse-menu.1",0);
	accordionOpen("#collapse-menu.2",1);
	accordionOpen("#collapse-menu.3",2);
	accordionOpen("#collapse-menu.4",3);
})
}(window.jQuery);


!function ($) {
$(document).ready(function () {
	function accordionOpen(_id){
		$(_id).find('.accordion-toggle').on('click', function(event){
			_group = $(this).parent().parent().parent().find('.accordion-group').removeClass("open")
			var _group = $(this).parent().parent();
			_group.addClass("open")
		});
	}
	//accordionOpen(id 名稱,指定開啟選單號碼)
	accordionOpen("#collapse-ct");
})
}(window.jQuery);