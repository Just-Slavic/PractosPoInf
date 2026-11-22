package project.practospoinformatike.service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import project.practospoinformatike.dao.SellerRepository;
import project.practospoinformatike.dto.Account;
import project.practospoinformatike.dto.SignIn;
import project.practospoinformatike.dto.SignUp;
import project.practospoinformatike.entity.Seller;
import project.practospoinformatike.entity.UserDetails;

import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class SellerService {
    @PersistenceContext
    private EntityManager entityManager;
    private PasswordEncoder passwordEncoder;
    private final SellerRepository sellerRepository;
    private static final String EMAIL_PATTERN = "([A-Za-z0-9]{1,}[\\-]{0,1}[A-Za-z0-9]{1,}[\\.]{0,1}[A-Za-z0-9]{1,})+@([A-Za-z0-9]{1,}[\\-]{0,1}[A-Za-z0-9]{1,}[\\.]{0,1}[A-Za-z0-9]{1,})+[\\.]{1}[a-z]{2,4}";
    private Account account;

    @Autowired
    public SellerService(@Qualifier("sellerRepository")SellerRepository sellerRepository,
                         @Qualifier("passwordEncoder")PasswordEncoder passwordEncoder){
        this.sellerRepository = sellerRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public void starter(){
        Scanner scanner = new Scanner(System.in);
        while(true) {
            System.out.println("Выберите опцию(1 или 2):\n\t\t1. Вход\n\t\t2. Регистрация");
            String option = scanner.nextLine();
            switch(option){
                case "1":getDataLogin(); break;
                case "2":getDataCreate(); break;
                default: System.out.println("некорректный формат"); break;
            }
            if(option.equals("1") | option.equals("2")) break;
        }
    }

    public void getDataLogin() {
        Scanner scanner = new Scanner(System.in);
        SignIn dto;
        while (true) {
            try {
                System.out.println("Введите данные: ");
                System.out.print("\t\tПочта: ");
                String email = scanner.nextLine();
                Pattern pattern = Pattern.compile(EMAIL_PATTERN);
                Matcher matcher = pattern.matcher(email);
                if(matcher.matches()){
                    System.out.print("\t\tПароль: ");
                    String password = scanner.nextLine();
                    if(password.length()>5) {
                        dto = new SignIn(email, password);
                        loginSeller(dto);
                        break;
                    }else{
                        throw new RuntimeException("слишком короткий пароль");
                    }
                }else{
                    throw new RuntimeException("некорректная почта");
                }
            } catch (Exception e) {
                System.out.println(e+"\nВведите данные снова\n");
            }
        }
    }
    public void getDataCreate() {
        Scanner scanner = new Scanner(System.in);
        SignUp dto;
        while (true) {
            try {
                System.out.println("Введите данные: ");
                System.out.print("\t\tПочта: ");
                String email = scanner.nextLine();
                if(!isEmailAlreadyExists(email)){
                    System.out.print("\t\tПароль: ");
                    String password = scanner.nextLine();
                    if(password.length()>5) {
                        System.out.print("\t\tИмя: ");
                        String name = scanner.nextLine();
                        if(name.length()>1 && name.length()<50){
                            System.out.print("\t\tВозраст: ");
                            int age = scanner.nextInt();
                            if(age>13){
                                dto = new SignUp(email, password, name, age);
                                createSeller(dto);
                                break;
                            }else{
                                throw new RuntimeException("вы младше 14 лет");
                            }
                        }else{
                            throw new RuntimeException("ваше имя должно быть от 2 до 50 символов");
                        }
                    }else{
                        throw new RuntimeException("слишком короткий пароль");
                    }
                }else{
                    throw new RuntimeException("такой аккаунт уже существует или введена некорректная почта");
                }
            } catch (Exception e) {
                System.out.println(e+"\nВведите данные снова\n");
            }
        }
    }

    @Transactional(readOnly = true)
    public boolean isEmailAlreadyExists(String email) {
        Pattern pattern = Pattern.compile(EMAIL_PATTERN);
        Matcher matcher = pattern.matcher(email);
        if(matcher.matches()) {
            try {
                Long count = (Long) entityManager.createQuery("SELECT COUNT(s) FROM Seller s WHERE s.email=:email", Long.class)
                        .setParameter("email", email)
                        .getSingleResult();
                return count > 0;
            } catch (Exception e) {
                return false;
            }
        }else{
            return true;
        }
    }

    @Transactional(readOnly = true)
    public void loginSeller(SignIn dto){
        Seller seller;
        while(true) {
            try{
                seller = (Seller) entityManager.createQuery("SELECT s FROM Seller s WHERE s.email=:email")
                        .setParameter("email", dto.getEmail())
                        .getSingleResult();
                if(passwordEncoder.matches(dto.getPassword(), seller.getPassword())) {
                    account = new Account(seller.getEmail(), seller.getPassword()
                            ,seller.getDetails().getName(), seller.getDetails().getAge());
                    break;
                }else{
                    System.out.println("Неверный логин или пароль");
                    starter();
                }
            }catch(Exception e){
                System.out.println("Неверный логин или пароль");
                starter();
            }
        }
    }
    @Transactional
    public void createSeller(SignUp dto){
        Seller seller = new Seller();
        UserDetails userDetails = new UserDetails();
        userDetails.setName(dto.getName());
        userDetails.setAge(dto.getAge());
        seller.setEmail(dto.getEmail());
        seller.setPassword(passwordEncoder.encode(dto.getPassword()));
        seller.setDetails(userDetails);
        sellerRepository.save(seller);
        account = new Account(dto.getEmail(), dto.getPassword(), dto.getName(), dto.getAge());
    }
}
