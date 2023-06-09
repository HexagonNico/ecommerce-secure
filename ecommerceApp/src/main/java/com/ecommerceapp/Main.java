package com.ecommerceapp;

import com.ecommerceapp.servlet.*;
import org.apache.catalina.Context;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.startup.Tomcat;
import org.apache.catalina.servlets.DefaultServlet;

import jakarta.servlet.MultipartConfigElement;

import java.io.File;

public class Main {

    public static String IMAGE_LOCATION;
    public static void main(String[] args) throws LifecycleException {
        Tomcat tomcat = new Tomcat();
        tomcat.setBaseDir("temp");
        tomcat.setPort(8080);
        tomcat.getConnector();

        String contextPath = "";
        String docBase = new File("ecommerceApp").getAbsolutePath();

        Context context = tomcat.addContext(contextPath, docBase);

        Tomcat.addServlet(context, "registerServlet", new RegisterServlet());
        context.addServletMappingDecoded("/register", "registerServlet");

        Tomcat.addServlet(context, "userServlet", new UserServlet());
        context.addServletMappingDecoded("/userServlet", "userServlet");

        Tomcat.addServlet(context, "loginServlet", new UserServlet());
        context.addServletMappingDecoded("/loginServlet", "loginServlet");

        ProductServlet productServlet = new ProductServlet();

        IMAGE_LOCATION = docBase + "/images";   // the directory location where files will be stored
        long maxFileSize = -1L; // the maximum size allowed for uploaded files
        long maxRequestSize = -1L; // the maximum size allowed for multipart/form-data requests
        int fileSizeThreshold = 0; // the size threshold after which files will be written to disk
        MultipartConfigElement multipartConfigElement = new MultipartConfigElement(
           IMAGE_LOCATION,
           maxFileSize,
           maxRequestSize,
           fileSizeThreshold
        );

        Tomcat.addServlet(context, "productServlet", productServlet)
              .setMultipartConfigElement(multipartConfigElement);

        context.addServletMappingDecoded("/productServlet", "productServlet");


        Tomcat.addServlet(context, "getProductServlet", new GetProductServlet());
        context.addServletMappingDecoded("/getProductServlet", "getProductServlet");

        Tomcat.addServlet(context, "getImageServlet", new GetImageServlet());
        context.addServletMappingDecoded("/getImageServlet", "getImageServlet");

        Tomcat.addServlet(context, "getSingleProductServlet", new GetSingleProductServlet());
        context.addServletMappingDecoded("/getSingleProductServlet", "getSingleProductServlet");

        Tomcat.addServlet(context, "orderServlet", new OrderServlet());
        context.addServletMappingDecoded("/orderServlet", "orderServlet");

        Tomcat.addServlet(context, "getOrdersServlet", new GetOrdersServlet());
        context.addServletMappingDecoded("/getOrdersServlet", "getOrdersServlet");

        Tomcat.addServlet(context, "getProductsCustomer", new GetProductsCustomer());
        context.addServletMappingDecoded("/getProductsCustomer", "getProductsCustomer");

        Tomcat.addServlet(context, "getOrdersCustomerServlet", new GetOrdersCustomerServlet());
        context.addServletMappingDecoded("/getOrdersCustomerServlet", "getOrdersCustomerServlet");

        Tomcat.addServlet(context, "reviewServlet", new ReviewServlet());
        context.addServletMappingDecoded("/reviewServlet", "reviewServlet");

        Tomcat.addServlet(context, "default", new DefaultServlet());
        context.addServletMappingDecoded("/", "default");
        
        Tomcat.addServlet(context, "replyServlet", new ReplyServlet());
        context.addServletMappingDecoded("/replyServlet", "replyServlet");

        Tomcat.addServlet(context, "searchProductServlet", new SearchProductServlet());
        context.addServletMappingDecoded("/searchProductServlet", "searchProductServlet");

        Tomcat.addServlet(context, "searchProductCustomerServlet", new SearchProductCustomerServlet());
        context.addServletMappingDecoded("/searchProductCustomerServlet", "searchProductCustomerServlet");

        Tomcat.addServlet(context, "chatServlet", new ChatServlet());
        context.addServletMappingDecoded("/chatServlet", "chatServlet");

        Tomcat.addServlet(context, "vendorServlet", new VendorServlet());
        context.addServletMappingDecoded("/vendorServlet", "vendorServlet");

        
        tomcat.start();
        System.out.println("📡 HTTP Tomcat Embedded listening on port 8080!");
        tomcat.getServer().await();
    }
}