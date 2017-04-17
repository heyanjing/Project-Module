package com.he.module.util.web;

import com.google.code.kaptcha.servlet.KaptchaServlet;

import javax.servlet.annotation.WebServlet;

/**
 * 编写好Servlet之后，接下来要告诉Web容器有关于这个Servlet的一些信息。在Servlet 3.0中，可以使用标注(Annotation)来告知容器哪些Servlet会提供服务以及额外信息。例如在HelloServlet.java中：
 * @WebServlet("/hello.view")
 * public class HelloServlet extends HttpServlet {
 * 只要在Servlet上设置@WebServlet标注，容器就会自动读取当中的信息。上面的@WebServlet告诉容器，如果请求的URL是“/hello.view”，则由HelloServlet的实例提供服务。可以使用@WebServlet提供更多信息。
 * @WebServlet(
 * name="Hello",
 * urlPatterns={"/hello.view"},
 * loadOnStartup=1
 * )
 * public class HelloServlet extends HttpServlet {
 * 上面的@WebServlet告知容器，HelloServlet这个Servlet的名称是Hello，这是由name属性指定的，而如果客户端请求的URL是/hello.view，则由具Hello名称的Servlet来处理，这是由urlPatterns属性来指定的。在Java EE相关应用程序中使用标注时，可以记得的是，没有设置的属性通常会有默认值。例如，若没有设置@WebServlet的name属性，默认值会是Servlet的类完整名称。
 * 当应用程序启动后，事实上并没有创建所有的Servlet实例。容器会在首次请求需要某个Servlet服务时，才将对应的Servlet类实例化、进行初始化操作，然后再处理请求。这意味着第一次请求该Servlet的客户端，必须等待Servlet类实例化、进行初始动作所必须花费的时间，才真正得到请求的处理。
 * 如果希望应用程序启动时，就先将Servlet类载入、实例化并做好初始化动作，则可以使用loadOnStartup设置。设置大于0的值(默认值为-1)，表示启动应用程序后就要初始化Servlet(而不是实例化几个Servlet)。数字代表了Servlet的初始顺序，容器必须保证有较小数字的Servlet先初始化，在使用标注的情况下，如果有多个Servlet在设置loadOnStartup时使用了相同的数字，则容器实现厂商可以自行决定要如何载入哪个Servlet。
 */
@SuppressWarnings("serial")
@WebServlet(urlPatterns = "/kaptcha/kaptcha.jpg", asyncSupported = true, loadOnStartup = -1, name = "kaptcha", displayName = "kaptcha")
public class VerificationCodeServlet extends KaptchaServlet {

}
