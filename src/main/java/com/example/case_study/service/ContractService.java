package com.example.case_study.service;

import com.example.case_study.model.*;
import com.example.case_study.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ContractService implements IContractService {
    @Autowired
    private IContractRepository contractRepository;
    @Autowired
    private IEmployeeRepository employeeRepository;
    @Autowired
    private ICustomerRepository customerRepository;
    @Autowired
    private IRoomRepository roomRepository;
    @Autowired
    private IPaymentRepository paymentRepository;

    @Override
    public Page<Contract> displayList(Pageable pageable) {
        return contractRepository.findAllByFlagDeleteIsFalse(pageable);
    }

    @Override
    public void addContract(Contract contract) {
        contractRepository.save(contract);

    }

    @Override
    public void deleteContract(Integer id) {

    }

    @Override
    public Optional<Contract> getContractById(Integer id) {
        return contractRepository.getContractByIdAndFlagDeleteIsFalse(id);
    }

    @Override
    public Contract getContract(Contract contract) {
        contract.setEmployee(employeeRepository.findById(contract.getEmployee().getId()).get());
        contract.setCustomer(customerRepository.findById(contract.getCustomer().getId()).get());
        contract.setRoom(roomRepository.findById(contract.getCustomer().getId()).get());
        return contract;
    }

    @Override
    public boolean getContractToManager(int id) {
        if (contractRepository.getContractByIdAndFlagDeleteIsFalseAndManagerConfirmIsFalse(id).isPresent()) {
            Contract contract = contractRepository.getContractByIdAndFlagDeleteIsFalseAndManagerConfirmIsFalse(id).get();
            contract.setManagerConfirm(true);
            contractRepository.save(contract);
            return true;
        }
        return false;
    }

    @Override
    public boolean getContractToDirector(int id) {
        if (contractRepository.getContractByIdAndFlagDeleteIsFalseAndManagerConfirmIsTrueAndDirectorConfirmIsFalse(id).isPresent()) {
            Contract contract = contractRepository.getContractByIdAndFlagDeleteIsFalseAndManagerConfirmIsTrueAndDirectorConfirmIsFalse(id).get();
            contract.setDirectorConfirm(true);
            contractRepository.save(contract);
            int a = 0;
            switch (contract.getKindContract().getId()) {
                case 1:
                    a = 1;
                    break;
                case 2:
                    a = 3;
                    break;
                case 3:
                    a = 12;
                    break;
                default:
                    break;
            }
            a *= contract.getPeriod();
            for (int i = 1; i <= a; i++) {
                paymentRepository.addPayment(i, contract.getId());
            }
            return true;
        }
        return false;
    }

    @Override
    public Page<Contract> getListToAccountant(Pageable pageable) {
        return contractRepository.findAllByFlagDeleteIsFalseAndManagerConfirmIsTrueAndDirectorConfirmIsTrue(pageable);
    }

    @Override
    public List<Contract> findSuccessContract() {
        return contractRepository.findAllByFlagDeleteFalseAndManagerConfirmIsTrueAndDirectorConfirmIsTrue();
    }

    @Override
    public List<Contract> findNotSuccessContract() {
        return contractRepository.findAllByFlagDeleteFalseAndManagerConfirmIsFalseOrDirectorConfirmIsFalse();
    }

    @Override
    public List<Contract> findContractHome() {
        return contractRepository.findAllByFlagDeleteFalse();
    }
}
