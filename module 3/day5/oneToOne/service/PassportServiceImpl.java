package org.example.associationdemojpa.oneToOne.service;

import org.example.associationdemojpa.oneToOne.entity.Passport;
import org.example.associationdemojpa.oneToOne.entity.Person;
import org.example.associationdemojpa.oneToOne.repository.PassportRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PassportServiceImpl implements PassportService{

    @Autowired
    public PassportRepo passportRepo;

    @Override
    public Passport addPassport(Passport passport) {
        return passportRepo.save(passport);
    }

    @Override
    public List<Passport> getAllPassports() {
        return passportRepo.findAll();
    }
//If id doesn't exist:NoSuchElementException
//   without get(),use  orElseThrow(()->throw new RunTimeException("not found"))
    @Override
    public Passport getPassportById(Long passportNumber) {
        return passportRepo.findById(passportNumber).get();
    }



    /*
    * For now , i have 2 fields : id and person in passport
    * if passport has more fields this will not work
    *update those also
    * STEPS
1. Find existing entity by ID
2. Throw exception if not found
3. Update mutable fields
4. Never update primary key
5. save(existing)
    * */

    /*
    You must maintain both sides of the relationship.

    JPA does not automatically keep both sides in sync.
*/
    @Override
    public Passport updatePassport(Long id, Passport passport) {
        Passport temp=passportRepo.findById(id).get();

//        Person person=temp.getPerson();  --> this resets the old value for the new value
//        temp.setPerson(person);

//        temp.setPerson(passport.getPerson());  --> TransientPropertyValueException

        Person existing=temp.getPerson();

        existing.setFname(passport.getPerson().getFname());
        existing.setLname(passport.getPerson().getLname());

        return passportRepo.save(temp);
    }

    @Override
    public void deletePassport(Long passportNumber) {
        passportRepo.deleteById(passportNumber);

    }
}
