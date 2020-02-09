package com.pkaushik.safeHome.service.impl;

import com.pkaushik.safeHome.SafeHomeApplication;
import com.pkaushik.safeHome.model.Student;
import com.pkaushik.safeHome.model.UserRole;
import com.pkaushik.safeHome.model.SafeHomeUser;
import com.pkaushik.safeHome.model.Walker;
import com.pkaushik.safeHome.model.enumerations.RequestStatus;
import com.pkaushik.safeHome.model.enumerations.WalkerStatus;
import com.pkaushik.safeHome.repository.WalkerRepository;
import com.pkaushik.safeHome.service.UserAuthServiceIF;
import com.pkaushik.safeHome.repository.SafeHomeUserRepository;
import com.pkaushik.safeHome.repository.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.util.Optional;
import java.util.stream.Stream;

@Service
public class UserAuthService implements UserAuthServiceIF{

    @Autowired
    private WalkerRepository walkerRepo;

    @Autowired
    private StudentRepository studentRepo;

    @Autowired
    private SafeHomeUserRepository userRepo;


/*     public UserAuthService(){
        //should we define a constructor in which we get all user data into memory?
        //we need all walkers, all students, and all users at this time. 
        allUsers = StreamSupport.stream(userRepo.findAll().spliterator(), false); 
        allWalkers = StreamSupport.stream(walkerRepo.findAll().spliterator(), false); 
        allStudents = StreamSupport.stream(studentRepo.findAll().spliterator(), false); 
    } */

    public void registerService(BigInteger phoneNo, int mcgillID, boolean regAsWlkr) {

         //already registered in this specific role?
        if (regAsWlkr) {
            if (walkerRepo.existsById(mcgillID)) throw new IllegalStateException("exists");
        } else {
            if (studentRepo.existsById(mcgillID)) throw new IllegalStateException("exists");
        }

        //2. need to create a user, with user role, and userrole impl
        //at this point the role does not exist, since that exception is taken care of already.

        SafeHomeUser user = null;
        Walker walkerRole = null;
        Student studentRole = null;

        if (regAsWlkr) {
            walkerRole = new Walker(mcgillID, false);
        } else {
            studentRole = new Student(mcgillID);
        }

        if (userRepo.existsById(mcgillID)) {
            if(userRepo.findById(mcgillID).isPresent()){
            user = userRepo.findById(mcgillID).get();}
            else{
                throw new IllegalStateException("You are not registered as a SafeHomeUser");
            }
        } else {
            user = new SafeHomeUser(phoneNo, mcgillID);
        }
        //user exists already, find the mcgillID and add the corresponding role to user
        //we're adding another role to this user.
        System.gc();
        if (regAsWlkr) {
            user.addRole(walkerRole);
            //save walker to walkerrepo
            walkerRepo.save(walkerRole);
        } else {
            user.addRole(studentRole);
            //save student to studentrepo
            studentRepo.save(studentRole);
        }

        userRepo.save(user);
    }
    //User -> User ID, Phone No, Roles should be stored too, way to deserialize that.

    public void loginService(int mcgillID, boolean loginAsWalker) {

        Optional<SafeHomeUser> userOp = userRepo.findById(mcgillID);

        if (userOp.isPresent()) {

            //TODO: Data model is user pk = student pk = walker pk = mc mcgillID

            if (loginAsWalker) {
                //check if walker exists with mcgillID    that walker is set with
                if (walkerRepo.findById(mcgillID).isPresent()) {
                    //walker exists, set logged in and set status.
                    Walker walker = walkerRepo.findById(mcgillID).get();
                    SafeHomeApplication.logInUser(mcgillID, walker);
                    walker.setStatus(WalkerStatus.LOGGED_IN);
                    walkerRepo.save(walker);
                } else {
                    //chose to login as a walker, and walker role does not exist
                    throw new IllegalStateException("You are not registered as a Walker");
                }
            } else {
                //check if student exists with mcgillID that student is set with
                if (studentRepo.findById(mcgillID).isPresent()) {
                    Student student = studentRepo.findById(mcgillID).get();
                    SafeHomeApplication.logInUser(mcgillID, student);
                    studentRepo.save(student);
                }
            }
        } else {
            //user is not reg at all
            throw new IllegalStateException("You are not registered");
        }
    }

    public void logoutService(int mcgillID){

        //validation: user must be currently logged in.

        if(!SafeHomeApplication.getLoggedInUsersMap().containsKey(mcgillID))
            throw new IllegalStateException("You can not log out without logging in first. ");

        //get the user role, that's currently logged in. 

        UserRole loggedInRole = SafeHomeApplication.getLoggedInUsersMap().get(mcgillID); 
		if(loggedInRole == null) throw new IllegalStateException("Logged in user could not be found");
		
		if(loggedInRole instanceof Walker){
            ((Walker) loggedInRole).setStatus(WalkerStatus.INACTIVE);
            ((Walker) loggedInRole).setCurrentAssignment(null); //change to cancel current assignment
            
            walkerRepo.save((Walker) loggedInRole); 
		}
		else if(loggedInRole instanceof Student){
            ((Student) loggedInRole).getRequest().setAssignment(null);
            ((Student) loggedInRole).getRequest().setRequestStatus(RequestStatus.COMPLETE);
            ((Student) loggedInRole).setRequest(null);
            
            studentRepo.save((Student) loggedInRole); 
			//set their status as well to inactive. 
		}
		else{
			return;
		}
		SafeHomeApplication.logOutUser(mcgillID);

        //what we need to save from here is that walker is inactive now and student.
        //todo: should we store currentRoleLoggedIn as property for user.
    }

    public void switchRoleService(int mcgillID){
        
     if(SafeHomeApplication.getLoggedInUsersMap().get(mcgillID) == null) 
		throw new IllegalAccessError("Can't switch roles without first logging in");

	UserRole currRole = SafeHomeApplication.getLoggedInUsersMap().get(mcgillID);
	
	if(currRole instanceof Student){
		logoutService(mcgillID);
		loginService(mcgillID, true);
	}
	else if(currRole instanceof Walker){
		logoutService(mcgillID);
		loginService(mcgillID, false);
	}
	else{
		throw new RuntimeException("Current role is neither a walker nor student"); 
	}
    //refreshcontext.
    
    }
}
