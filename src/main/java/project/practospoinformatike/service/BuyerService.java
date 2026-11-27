package project.practospoinformatike.service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import project.practospoinformatike.dao.BuyerRepository;
import project.practospoinformatike.dto.Account;
import project.practospoinformatike.dto.SignIn;
import project.practospoinformatike.dto.SignUp;
import project.practospoinformatike.entity.Buyer;
import project.practospoinformatike.entity.UserDetails;

import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class BuyerService {
    @PersistenceContext
    private EntityManager entityManager;
    private final PasswordEncoder passwordEncoder;
    private final BuyerRepository buyerRepository;
    private static final String EMAIL_PATTERN = "([A-Za-z0-9]{1,}[\\-]{0,1}[A-Za-z0-9]{1,}[\\.]{0,1}[A-Za-z0-9]{1,})+@([A-Za-z0-9]{1,}[\\-]{0,1}[A-Za-z0-9]{1,}[\\.]{0,1}[A-Za-z0-9]{1,})+[\\.]{1}[a-z]{2,4}";
    private Account account;
    @Autowired
    public BuyerService(@Qualifier("buyerRepository")BuyerRepository buyerRepository,
                        @Qualifier("passwordEncoder")PasswordEncoder passwordEncoder){
        this.buyerRepository = buyerRepository;
        this.passwordEncoder = passwordEncoder;
    }
    //стартовое меню
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
    //получение данных для входа
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
                        loginBuyer(dto);
                        buyerMenu();
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
    //получение данных для нового профиля
    public void getDataCreate() {
        Scanner scanner = new Scanner(System.in);
        SignUp dto;
        String email, password, name;
        int age;
        while (true) {
            try {
                System.out.println("Введите данные: ");
                System.out.print("\t\tПочта: ");
                email = scanner.nextLine();
                if(!isEmailAlreadyExists(email)){
                    System.out.print("\t\tПароль: ");
                    password = scanner.nextLine();
                }else{
                    throw new RuntimeException("такой аккаунт уже существует или введена некорректная почта");
                }
                if(password.length()>5) {
                    System.out.print("\t\tИмя: ");
                    name = scanner.nextLine();
                }else{
                    throw new RuntimeException("слишком короткий пароль");
                }
                if(name.length()>1 && name.length()<50){
                    System.out.print("\t\tВозраст: ");
                    age = scanner.nextInt();
                }else{
                    throw new RuntimeException("ваше имя должно быть от 2 до 50 символов");
                }
                if(age>13){
                    dto = new SignUp(email, password, name, age);
                    createBuyer(dto);
                    buyerMenu();
                    break;
                }else{
                    throw new RuntimeException("вы младше 14 лет");
                }
            } catch (Exception e) {
                System.out.println(e+"\nВведите данные снова\n");
            }
        }
    }
    //проверка сущесвтования схожей почты
    @Transactional(readOnly = true)
    public boolean isEmailAlreadyExists(String email) {
        Pattern pattern = Pattern.compile(EMAIL_PATTERN);
        Matcher matcher = pattern.matcher(email);
        if(matcher.matches()) {
            try {
                Long count = (Long) entityManager.createQuery("SELECT COUNT(b) FROM Buyer b WHERE b.email=:email", Long.class)
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
    //вход в аккаунт покупателя
    @Transactional(readOnly = true)
    public void loginBuyer(SignIn dto){
        Buyer buyer;
        while(true) {
            try{
                buyer = (Buyer) entityManager.createQuery("SELECT b FROM Buyer b WHERE b.email=:email", Buyer.class)
                        .setParameter("email", dto.getEmail())
                        .getSingleResult();
                if(passwordEncoder.matches(dto.getPassword(), buyer.getPassword())) {
                    account = new Account(buyer.getEmail(), buyer.getPassword()
                            ,buyer.getUserDetails().getName(), buyer.getUserDetails().getAge());
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
    //создание покупателя
    @Transactional
    public void createBuyer(SignUp dto){
        Buyer buyer = new Buyer();
        UserDetails userDetails = new UserDetails();
        userDetails.setName(dto.getName());
        userDetails.setAge(dto.getAge());
        buyer.setEmail(dto.getEmail());
        buyer.setPassword(passwordEncoder.encode(dto.getPassword()));
        buyer.setUserDetails(userDetails);
        buyerRepository.save(buyer);
        account = new Account(dto.getEmail(), dto.getPassword(), dto.getName(), dto.getAge());
    }
    //меню пользователя
    @Transactional
    public void buyerMenu(){
        Scanner scanner = new Scanner(System.in);
        while(true) {
            String option = "";
            try {
                System.out.println("Выберите опцию: " +
                        "\n\t\t 1. Редактировать профиль." +
                        "\n\t\t 2. Список товаров." +
                        "\n\t\t 3. Покупки." +
                        "\n\t\t 4. Завершить программу." +
                        "\n\t\t 5. Удалить  аккаунт.");
                option = scanner.nextLine();
                switch (option) {
                    case "1": getDataToChangeProfile(); break;
                    case "2": break;
                    case "3": break;
                    case "4": throw new RuntimeException("Программа завершена");
                    case "5": deleteAccount(account.getEmail()); throw new RuntimeException("Аккаунт удален");
                    default: throw new RuntimeException("Некорректный вариант");
                }
            } catch (Exception e) {
                System.out.println("\n__________________________________________________________\n");
                if(option.equals("4") | option.equals("5")) {
                    break;
                }
            }
        }
    }
    //удалить аккаунт
    @Transactional
    public void deleteAccount(String email){
        buyerRepository.deleteByEmail(email);

    }
    //получить данные для смены данных профиля
    public void getDataToChangeProfile(){
        Scanner scanner = new Scanner(System.in);
        SignUp dto;
        String email ,name;
        int age;
        while (true) {
            try {
                System.out.println("Введите новые данные: ");
                System.out.print("\t\tПочта: ");
                email = scanner.nextLine();
                if(!isEmailAlreadyExists(email)){
                    System.out.print("\t\tИмя: ");
                    name = scanner.nextLine();
                }else{
                    throw new RuntimeException("такой аккаунт уже существует или введена некорректная почта");
                }
                if(name.length()>1 && name.length()<50){
                    System.out.print("\t\tВозраст: ");
                    age = scanner.nextInt();
                }else{
                    throw new RuntimeException("ваше имя должно быть от 2 до 50 символов");
                }
                if(age>13){
                    if(changeBuyerData(name, email, age)){
                        account.setAge(age);
                        account.setEmail(email);
                        account.setName(name);
                    }
                    buyerMenu();
                    break;
                }else{
                    throw new RuntimeException("вы младше 14 лет");
                }
            } catch (Exception e) {
                System.out.println(e+"\nВведите данные снова\n");
            }
        }
    }
    @Transactional
    public boolean changeBuyerData(String name, String email, int age){
        Buyer buyer = entityManager.createQuery("SELECT b FROM Buyer b WHERE b.email=:email", Buyer.class)
                .setParameter("email", account.getEmail())
                .getSingleResult();
        buyer.setEmail(email);
        buyer.getUserDetails().setName(name);
        buyer.getUserDetails().setAge(age);
        buyerRepository.save(buyer);
        return true;
    }
}
