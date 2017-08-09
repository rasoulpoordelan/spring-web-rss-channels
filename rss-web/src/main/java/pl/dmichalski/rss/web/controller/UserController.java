package pl.dmichalski.rss.web.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import pl.dmichalski.rss.core.entity.RssFeedEntity;
import pl.dmichalski.rss.core.service.IRssFeedService;
import pl.dmichalski.rss.core.service.IUserService;

import javax.validation.Valid;
import java.security.Principal;

/**
 * Author: Daniel
 */
@Controller
public class UserController {

    private IUserService userService;

    private IRssFeedService blogService;

    @Autowired
    public UserController(IUserService userService, IRssFeedService blogService) {
        this.userService = userService;
        this.blogService = blogService;
    }

    @RequestMapping("account")
    public String account(Model model, Principal principal) {
        String userName = principal.getName();
        model.addAttribute("blog", new RssFeedEntity());
        model.addAttribute("user", userService.findOneWithAllBlogs(userName));
        return "account";
    }

    @RequestMapping(value = "account", method = RequestMethod.POST)
    public String doAddBlog(@Valid @ModelAttribute("blog") RssFeedEntity rssFeedEntity, Model model,
                            Principal principal, BindingResult result) {
        if (result.hasErrors()) {
            return account(model, principal);
        }
        String name = principal.getName();
        blogService.save(rssFeedEntity, name);
        return "redirect:/account";
    }

    @RequestMapping(value = "blog/remove/{id}")
    public String removeBlog(@PathVariable Long id) {
        RssFeedEntity rssFeedEntity = blogService.findOne(id);
        blogService.delete(rssFeedEntity);
        return "redirect:/account";

    }
    @RequestMapping(value = "reset")
    @ResponseBody
    public String refresh() {

        RssFeedEntity ff=new RssFeedEntity();
        ff.setId((long)3);
        ff.setName("salam");
        ff.setUrl("http://www.javaworld.com/index.rss");
        blogService.saveAll(ff);
       return "hello blog";
    }
}
