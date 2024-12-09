$(document).ready(function () {
    // Khai báo các biến và phần tử DOM cần sử dụng
    var $wrapper = $('.main-wrapper');
    var $slimScrolls = $('.slimscroll');
    var $pageWrapper = $('.page-wrapper');

    // Thay thế các biểu tượng của Feather Icons
    feather.replace();

    // Cập nhật chiều cao của page-wrapper khi thay đổi kích thước cửa sổ
    $(window).resize(function () {
        if ($('.page-wrapper').length > 0) {
            var height = $(window).height();
            $(".page-wrapper").css("min-height", height);
        }
    });

    // Thêm overlay cho sidebar khi menu di động mở
    $('body').append('<div class="sidebar-overlay"></div>');

    // Tạo sự kiện cho nút mobile menu
    $(document).on('click', '#mobile_btn', function () {
        $wrapper.toggleClass('slide-nav');
        $('.sidebar-overlay').toggleClass('opened');
        $('html').addClass('menu-opened');
        $('#task_window').removeClass('opened');
        return false;
    });

    // Đóng sidebar khi click vào overlay
    $(".sidebar-overlay").on("click", function () {
        $('html').removeClass('menu-opened');
        $(this).removeClass('opened');
        $wrapper.removeClass('slide-nav');
        $('.sidebar-overlay').removeClass('opened');
        $('#task_window').removeClass('opened');
    });

    // Ẩn các phần tử khi click vào các nút có class "hideset" hoặc "delete-set"
    $(document).on("click", ".hideset", function () {
        $(this).parent().parent().parent().hide();
    });

    $(document).on("click", ".delete-set", function () {
        $(this).parent().parent().hide();
    });

    // Cấu hình carousel cho sản phẩm
    if ($('.product-slide').length > 0) {
        $('.product-slide').owlCarousel({
            items: 1,
            margin: 30,
            dots: false,
            nav: true,
            loop: false,
            responsiveClass: true,
            responsive: {
                0: { items: 1 },
                800: { items: 1 },
                1170: { items: 1 }
            }
        });
    }

    // Cập nhật ảnh khi người dùng chọn tệp hình ảnh mới
    function readURL(input) {
        if (input.files && input.files[0]) {
            var reader = new FileReader();
            reader.onload = function (e) {
                $('#blah').attr('src', e.target.result);
            }
            reader.readAsDataURL(input.files[0]);
        }
    }

    // Ẩn/hiện loader sau một thời gian
    setTimeout(function () {
        $('#global-loader');
        setTimeout(function () {
            $("#global-loader").fadeOut("slow");
        }, 100);
    }, 500);

    // Cấu hình datetimepicker
    if ($('.datetimepicker').length > 0) {
        $('.datetimepicker').datetimepicker({
            format: 'DD-MM-YYYY',
            icons: {
                up: "fas fa-angle-up",
                down: "fas fa-angle-down",
                next: 'fas fa-angle-right',
                previous: 'fas fa-angle-left'
            }
        });
    }

    // Cấu hình toggle password visibility
    if ($('.toggle-password').length > 0) {
        $(document).on('click', '.toggle-password', function () {
            $(this).toggleClass("fa-eye fa-eye-slash");
            var input = $(".pass-input");
            if (input.attr("type") == "password") {
                input.attr("type", "text");
            } else {
                input.attr("type", "password");
            }
        });
    }

    // Cấu hình select2 cho các dropdowns
    if ($('.select').length > 0) {
        $('.select').select2({
            minimumResultsForSearch: -1,
            width: '100%'
        });
    }

    // Cập nhật số lượng khi sử dụng counter
    if ($('.counter').length > 0) {
        $('.counter').counterUp({
            delay: 20,
            time: 2000
        });
    }

    // Cấu hình các bộ đếm thời gian (countdown, countup, v.v.)
    if ($('#timer-countdown').length > 0) {
        $('#timer-countdown').countdown({
            from: 180,
            to: 0,
            movingUnit: 1000,
            outputPattern: '$day Day $hour : $minute : $second',
            autostart: true
        });
    }

    // Cấu hình Summernote cho các textarea
    if ($('#summernote').length > 0) {
        $('#summernote').summernote({
            height: 300,
            minHeight: null,
            maxHeight: null,
            focus: true
        });
    }

    // Cấu hình slimScroll cho sidebar
    if ($slimScrolls.length > 0) {
        $slimScrolls.slimScroll({
            height: 'auto',
            width: '100%',
            position: 'right',
            size: '7px',
            color: '#ccc',
            wheelStep: 10,
            touchScrollStep: 100
        });
        var wHeight = $(window).height() - 60;
        $slimScrolls.height(wHeight);
        $('.sidebar .slimScrollDiv').height(wHeight);

        // Cập nhật lại chiều cao slimScroll khi cửa sổ thay đổi kích thước
        $(window).resize(function () {
            var rHeight = $(window).height() - 60;
            $slimScrolls.height(rHeight);
            $('.sidebar .slimScrollDiv').height(rHeight);
        });
    }

    // Cấu hình sidebar menu
    var Sidemenu = function () {
        this.$menuItem = $('#sidebar-menu a');
    };

    function init() {
        var $this = Sidemenu;
        $('#sidebar-menu a').on('click', function (e) {
            if ($(this).parent().hasClass('submenu')) {
                e.preventDefault();
            }
            if (!$(this).hasClass('subdrop')) {
                $('ul', $(this).parents('ul:first')).slideUp(250);
                $('a', $(this).parents('ul:first')).removeClass('subdrop');
                $(this).next('ul').slideDown(350);
                $(this).addClass('subdrop');
            } else if ($(this).hasClass('subdrop')) {
                $(this).removeClass('subdrop');
                $(this).next('ul').slideUp(350);
            }
        });

        // Mở submenu nếu đã có class active
        $('#sidebar-menu ul li.submenu a.active').parents('li:last').children('a:first').addClass('active').trigger('click');
    }

    init();

    // Sự kiện cho toggle sidebar
    $(document).on('click', '#toggle_btn', function () {
        if ($('body').hasClass('mini-sidebar')) {
            $('body').removeClass('mini-sidebar');
            $(this).addClass('active');
            $('.subdrop + ul').slideDown();
            localStorage.setItem('screenModeNightTokenState', 'night');
            setTimeout(function () {
                $("body").removeClass("mini-sidebar");
                $(".header-left").addClass("active");
            }, 100);
        } else {
            $('body').addClass('mini-sidebar');
            $(this).removeClass('active');
            $('.subdrop + ul').slideUp();
            localStorage.removeItem('screenModeNightTokenState', 'night');
            setTimeout(function () {
                $("body").addClass("mini-sidebar");
                $(".header-left").removeClass("active");
            }, 100);
        }
        return false;
    });

    // Thay đổi giao diện giữa Light/Dark Mode
    if (localStorage.getItem('screenModeNightTokenState') == 'night') {
        setTimeout(function () {
            $("body").removeClass("mini-sidebar");
            $(".header-left").addClass("active");
        }, 100);
    }

    // Các sự kiện bổ sung cho các nút giao diện khác như toggle settings, sidebar views, v.v.
    $('.open-layout').on("click", function (s) {
        s.preventDefault();
        $('.sidebar-layout').addClass('show-layout');
        $('.sidebar-settings').removeClass('show-settings');
    });

    // Đóng layout khi nhấn nút đóng
    $('.btn-closed').on("click", function (s) {
        s.preventDefault();
        $('.sidebar-layout').removeClass('show-layout');
    });

    // Cấu hình theme cho trang web
    if ($('.toggle-switch').length > 0) {
        const toggleSwitch = document.querySelector('.toggle-switch input[type="checkbox"]');
        const currentTheme = localStorage.getItem('theme');
        var app = document.getElementsByTagName("BODY")[0];

        if (currentTheme) {
            app.setAttribute('data-theme', currentTheme);
            if (currentTheme === 'dark') {
                toggleSwitch.checked = true;
            }
        }

        function switchTheme(e) {
            if (e.target.checked) {
                app.setAttribute('data-theme', 'dark');
                localStorage.setItem('theme', 'dark');
            } else {
                app.setAttribute('data-theme', 'light');
                localStorage.setItem('theme', 'light');
            }
        }
        toggleSwitch.addEventListener('change', switchTheme, false);
    }

    // Quản lý tab UI
    $('ul.tabs li').click(function () {
        var $this = $(this);
        var $theTab = $(this).attr('id');
        if (!$this.hasClass('active')) {
            $this.closest('.tabs_wrapper').find('ul.tabs li, .tabs_container .tab_content').removeClass('active');
            $('.tabs_container .tab_content[data-tab="' + $theTab + '"], ul.tabs li[id="' + $theTab + '"]').addClass('active');
        }
    });
});
