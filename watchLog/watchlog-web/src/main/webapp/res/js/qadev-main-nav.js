$.fn.qadev_main_nav = function(platform_name, user_name) {
  function get_platforms() {
    if (typeof(main_nav_platforms) == "undefined")
      return [
        {
          "PTP" : ["性能测试平台", "http://perf.hz.netease.com"],
          "AB" : ["引流自动化平台", "http://ab.hz.netease.com"],
          "WL" : ["日志监控系统", "http://watchlog.hz.netease.com"],
        }, {
          "QMS" : ["质量度量平台", "http://qms.hz.netease.com"],
          "TC" : ["用例管理平台", "http://tc.hz.netease.com"],
          "XX1" : ["渠道发布平台", ""],
          "XX2" : ["开发者服务", ""],
        }, {
          "MSP" : ["移动测试中心", "http://10086.netease.com"],
          "CB" : ["云测平台", "http://omtl.hz.netease.com"],
          "CT" : ["众测平台", "http://app.hz.netease.com"],
        }, {
          "JENKINS" : ["Jenkins", "http://ci.hz.netease.com"],
          "SONAR" : ["Sonar", "http://sonar.hz.netease.com"],
          "WP" : ["QA白皮书", "http://doc.hz.netease.com/display/QAWhitePaper/Home"],
        }
      ];
    else
      return main_nav_platforms;
  }

  function nav_li(platform) {
    var li = $("<li></li>");
    var a = $("<a target='_blank'></a>");
    a.text(platform[0]);
    a.attr("href", platform[1]);
    li.append(a);
    return li;
  }

  function sub_nav_li(platform) {
    var li = $("<div></div>");
    var a = $("<a target='_blank'></a>");
    a.text(platform[0]);
    a.attr("href", platform[1]);
    li.append(a);
    return li;
  }

  function make_div_qadev_nav(main_div, platform_name, user_name) {
    var out_div = $("<div></div>",{class:"qadev-nav-out-div"});
    var container = $("<div></div>",{class:"qadev-nav-container"});
    var logo = $("<img></img>",{class:"qadev-nav-logo",src:"/res/img/logo.png"});
    var nav_bar = $("<div></div>",{class:"qadev-nav-bar"});
    var auth_bar = $("<div></div>",{class:"qadev-nav-link"});

    var nav_ul = $("<ul></ul>");
    var nav_sub = $("<li></li>",{class:"nav-dropdown-li"});
    var nav_dropdown_icon = $("<a><b class='qadev-nav-icn qadev-nav-pos-icn'></b></a>");
    var sub_nav_container = $("<div></div>",{class:"qadev-nav-dropdown-container qadev-nav-display"});
    var sub_nav_ul = $("<div></div>",{class:"qadev-nav-sub-nav-bar"});
    var underline = $("<div></div>",{class:"qadev-nav-underline"});

    var platforms = get_platforms();
    var cur_category;

    var i,j;

    for(i=0; i<platforms.length; i++) {
      if(platforms[i][platform_name] != null) {
        cur_category = platforms.splice(i,1)[0];
        break;
      }
    }

    if(cur_category == null) {
      cur_category = platforms.splice(0,1)[0];
      platform_name = null;
    }

    $(main_div).append(out_div);
    out_div.append(container);
    container.append(logo);
    container.append(nav_bar);
    container.append(auth_bar);
    nav_bar.append(nav_ul);

    if(platform_name != null) {
      cur_li = nav_li(cur_category[platform_name]);
      cur_li.addClass("qadev-nav-active");
      nav_ul.append(cur_li);
    }
    for(i in cur_category) {
      if(i==platform_name)
        continue;
      nav_ul.append(nav_li(cur_category[i]));
    }

    nav_ul.append(nav_sub);
    nav_sub.append(nav_dropdown_icon);
    nav_sub.append(sub_nav_container);
    sub_nav_container.append(sub_nav_ul);

    for(i=0; i<platforms.length; i++) {
      if(i != 0)
        sub_nav_ul.append(underline.clone());
      c = platforms[i];
      for(j in c) {
        sub_nav_ul.append(sub_nav_li(c[j]));
      }
    }

    $(document).click(function () {
      $(sub_nav_container).hide(200);
    });
    $(nav_dropdown_icon).click(function (event) {
      event.stopPropagation();
      $(sub_nav_container).toggle(200);
    });

    if(user_name != null) {
      var auth_dropdown_icon = $("<a style='cursor: pointer;'><b class='qadev-nav-icn qadev-nav-user-icn qadev-nav-user-icon'></b>"+user_name+"</a>");
      var auth_container = $("<div></div>",{class:"qadev-nav-dropdown-container qadev-nav-auth-container qadev-nav-display"});
      var auth_ul = $("<div class='qadev-nav-sub-nav-bar'><div><a href='/logout'>退出</a></div></div>");
      auth_bar.append(auth_dropdown_icon);
      auth_bar.append(auth_container);
      auth_container.append(auth_ul);
      $(document).click(function () {
        $(auth_container).hide(200);
      });
      $(auth_dropdown_icon).click(function (event) {
        event.stopPropagation();
        $(auth_container).toggle(200);
      });
    } else {
      var auth_login_icon = $("<a href='/login'>登录</a>");
      auth_bar.append(auth_login_icon);
    }

    return out_div;

  }

  if($(this).get(0).tagName != "DIV")
    return;

  var nav = make_div_qadev_nav(this, platform_name, user_name);
  nav.css("margin-top", "-"+$("body").css("margin-top"));
  nav.css("margin-left", "-"+$("body").css("margin-left"));
  nav.css("margin-right", "-"+$("body").css("margin-right"));
}
