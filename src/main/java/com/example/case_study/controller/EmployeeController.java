package com.example.case_study.controller;

import com.example.case_study.dto.EmployeeDto;
import com.example.case_study.model.Employee;
import com.example.case_study.service.employee.IEmployeeService;
import com.example.case_study.service.employee.IPositionService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.util.List;

@Controller
@RequestMapping("/employee")
public class EmployeeController {
    @Autowired
    private IEmployeeService employeeService;
    @Autowired
    private IPositionService positionService;

    @GetMapping()
    public String showListEmployee(@PageableDefault(size = 2, sort = "id", direction = Sort.Direction.DESC) Pageable pageable,
                                   Model model) {
         model.addAttribute("employees", employeeService.displayListEmployee(pageable));

        return "employee/el";
    }

    @GetMapping("create")
    public String showFormCreate(Model model) {
      model.addAttribute("employeeDto",new EmployeeDto());
      model.addAttribute("positionList",positionService.displayListRole());
        return "employee/create";
    }

    @PostMapping("create")
    public String createNewEmployee(@Valid @ModelAttribute EmployeeDto employeeDto, BindingResult bindingResult,
                                    Model model, RedirectAttributes redirectAttributes) {
        new EmployeeDto().validate(employeeDto, bindingResult);
        if (bindingResult.hasErrors()) {
            model.addAttribute("positionList",positionService.displayListRole());
//            model.addAttribute("employeeDto",employeeDto);
                return "employee/create";
        }
        Employee employee = new Employee();
        BeanUtils.copyProperties(employeeDto, employee);
        employeeService.createEmployee(employee);
        redirectAttributes.addFlashAttribute("message","Create finish");
        return "redirect:/employee";
    }
    @PostMapping("/delete/{id}")
    public String deleteEmployee(@PathVariable int id,RedirectAttributes redirectAttributes){
        if (employeeService.deleteEmployee(id)){
            redirectAttributes.addFlashAttribute("message", "Xoá Thành công");
        }else {
            redirectAttributes.addFlashAttribute("message", "Không tìm thấy id");
        }
        return "redirect:/employee";
    }
    @GetMapping("/edit/{id}")
    public String showFormEditEmployee(@PathVariable int id,RedirectAttributes redirectAttributes,Model model){
        if (employeeService.getEmployeeById(id) == null){
            redirectAttributes.addFlashAttribute("message","Find not Found id");
            return "redirect:/employee";
        }else {
            EmployeeDto employeeDto = new EmployeeDto();
            BeanUtils.copyProperties(employeeService.getEmployeeById(id),employeeDto);
            model.addAttribute("employeeDto",employeeDto);
            model.addAttribute("positionList",positionService.displayListRole());
            return "employee/edit";
        }
    }
    @PostMapping("/edit")
    public String editEmployee(@Valid @ModelAttribute EmployeeDto employeeDto,BindingResult bindingResult,Model model,RedirectAttributes redirectAttributes){
        new EmployeeDto().validate(employeeDto,bindingResult);
        if (bindingResult.hasErrors()){
            return "employee/edit";
        }
        Employee employee = new Employee();
        BeanUtils.copyProperties(employeeDto,employee);
        employeeService.editEmployee(employee);
        redirectAttributes.addFlashAttribute("message","Fixed");
                return "redirect:/employee";
    }
    @PostMapping("/search")
    public String search(@RequestParam("name") String name,@RequestParam("citizenId") String citizenId,@RequestParam("phoneNumber") String phoneNumber,Pageable pageable,Model model){
        Page<Employee> employees = employeeService.searchEmployeeByName(pageable, name,citizenId,phoneNumber);
        model.addAttribute("employees",employees);
        return "employee/el";
    }
}
