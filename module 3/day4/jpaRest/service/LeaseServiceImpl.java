package org.example.jpademo.service;

import org.example.jpademo.model.Lease;
import org.example.jpademo.repository.LeaseRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.LinkedList;
import java.util.List;

@Service
public class LeaseServiceImpl implements LeaseService{

    @Autowired
    private LeaseRepo leaseRepo;

    @Override
    public Lease save(Lease lease) {
        return leaseRepo.save(lease);
    }

    @Override
    public void deleteById(int id) {
        leaseRepo.deleteById(id);

    }

    @Override
    public Lease findById(int id) {

        return leaseRepo.findById(id).get();
    }

    @Override
    public List<Lease> findAll() {
        return leaseRepo.findAll();
    }


    @Override
    public Lease updateById(int id, Lease lease) {
        Lease l=leaseRepo.findById(id).get();

                l.setCustomerName(lease.getCustomerName());
                l.setTenureMonths(lease.getTenureMonths());
                l.setAssetType(lease.getAssetType());
                l.setEmi(lease.getEmi());
                l.setLeaseAmt(lease.getLeaseAmt());
                l.setStatus(lease.getStatus());


        return leaseRepo.save(l);
    }
}
