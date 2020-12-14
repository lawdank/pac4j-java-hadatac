package org.hadatac.console.controllers;


import com.google.inject.Inject;
import play.mvc.Controller;
import play.mvc.Result;
import org.hadatac.console.providers.MyService;

public class SendMail extends Controller {
    @Inject
    private MyService service;

    public Result sendMail() {
        service.doMail();
        return ok("Mail sent!");
    }

}