package pl.wojtektrzos.filmkrecimy.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import pl.wojtektrzos.filmkrecimy.entity.User;
import pl.wojtektrzos.filmkrecimy.entity.UserDetails;
import pl.wojtektrzos.filmkrecimy.repository.EventDateRepository;
import pl.wojtektrzos.filmkrecimy.repository.PlanItemRoleRepository;
import pl.wojtektrzos.filmkrecimy.repository.UserDetailsRepository;
import pl.wojtektrzos.filmkrecimy.repository.UserRepository;
import pl.wojtektrzos.filmkrecimy.service.CurrentUser;
import pl.wojtektrzos.filmkrecimy.service.UserCalendarService;
import pl.wojtektrzos.filmkrecimy.service.UserServiceImpl;

import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/user")
public class UserController {
    @Autowired
    UserServiceImpl userRepository;
    @Autowired
    UserDetailsRepository userDetailsRepository;
    @Autowired
    PlanItemRoleRepository planItemRoleRepository;
    @Autowired
    UserCalendarService userCalendarService;
    @Autowired
    EventDateRepository eventDateRepository;


    @GetMapping("/register")

    public String initAddNewUser(Model model) {
        model.addAttribute("user", new User());
        return "views/user/register";
    }

    @PostMapping("/register")
    public String addNewUser(@Validated User user, BindingResult result) {
        if (result.hasErrors()) {
            return "views/user/register";
        }
        userRepository.saveUser(user);

        return "redirect:/login";
    }

    @GetMapping("/edit")
    @Secured("ROLE_USER")
    public String editUser(@AuthenticationPrincipal CurrentUser currentUser, Model model) {
        model.addAttribute("details", userDetailsRepository.findUserDetailsByLogInfo(currentUser.getUser()));
        model.addAttribute("planItemRoles", planItemRoleRepository.findAllByModifier("USERTAG"));
        return "views/user/edit";
    }

    @PostMapping("/edit")
    @Secured("ROLE_USER")
    public String updateUser(@AuthenticationPrincipal CurrentUser currentUser, @Validated UserDetails details, BindingResult result, RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return "views/user/edit";
        }
        currentUser.getUser().getDetails().setEmail(details.getEmail());
        userDetailsRepository.save(currentUser.getUser().getDetails());
        redirectAttributes.addFlashAttribute("message", "zaktualizowano email");
        return "redirect:/user/edit";
    }

    @GetMapping("/projects")
    @Secured("ROLE_USER")
    public String userProjects(@AuthenticationPrincipal CurrentUser currentUser, Model model) {
        model.addAttribute("userDates", eventDateRepository.findAllByOwnerPlanItem(currentUser.getUser().getDetails().getPlanMyself()));
        model.addAttribute("user", currentUser.getUser().getDetails());
        model.addAttribute("calendar", userCalendarService.getYear());
        return "views/user/projects";
    }

    @PostMapping("/updateAvailibleDates")
    @Secured("ROLE_USER")
    @ResponseBody
    public String updateAvailibleDates(@RequestParam String[] eventDates){
        LocalDate date = LocalDate.parse(eventDates[0]);
        return date.toString();
    }


}
