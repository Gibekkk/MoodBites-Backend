// package com.kritz.restfulapi.service;

// import java.util.Optional;
// import java.util.List;
// import java.util.ArrayList;
// import java.util.Map;
// import java.time.LocalDate;
// import java.time.LocalDateTime;
// import java.time.format.DateTimeFormatter;
// import java.time.Duration;

// import org.springframework.web.multipart.MultipartFile;

// import org.apache.commons.lang3.RandomStringUtils;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.beans.factory.annotation.Value;
// import org.springframework.stereotype.Service;
// import org.springframework.transaction.annotation.Transactional;

// import com.kritz.restfulapi.dto.BankDTO;
// import com.kritz.restfulapi.dto.UserDTO;
// import com.kritz.restfulapi.model.User;
// import com.kritz.restfulapi.model.BankUser;
// import com.kritz.restfulapi.model.InformasiUser;
// import com.kritz.restfulapi.model.DetailAlamat;
// import com.kritz.restfulapi.model.PembelianSubscription;
// import com.kritz.restfulapi.model.DetailSubscription;
// import com.kritz.restfulapi.model.FilterBroadcast;
// import com.kritz.restfulapi.repository.UserRepository;
// import com.kritz.restfulapi.repository.InformasiUserRepository;
// import com.kritz.restfulapi.repository.BankUserRepository;
// import com.kritz.restfulapi.repository.DetailAlamatRepository;
// import com.kritz.restfulapi.repository.DetailSubscriptionRepository;
// import com.kritz.restfulapi.util.PasswordHasherMatcher;

// @Service
// public class UserService {
//     @Autowired
//     private UserRepository userRepository;

//     @Autowired
//     private InformasiUserRepository informasiUserRepository;

//     @Autowired
//     private DetailAlamatRepository detailAlamatRepository;

//     @Autowired
//     private DetailSubscriptionRepository detailSubscriptionRepository;

//     @Autowired
//     private BankUserRepository bankUserRepository;

//     @Autowired
//     private PasswordHasherMatcher passwordMaker;

//     @Autowired
//     private ImageService imageService;

//     @Value("${storage.upload-dir}/profil/user/")
//     private String pathToFoto;

//     final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

//     public String getFoto(String objectId) {
//         Optional<User> optionalObject = findUserById(objectId);
//         return optionalObject.isPresent()
//                 ? imageService.getImage(Optional.ofNullable(optionalObject.get().getImageUrl()).orElse(""))
//                 : "";
//     }

//     public String getFotoTime(String imagePath) {
//         return imageService.getImageTimeStamp(imagePath);
//     }

//     public String getFotoTimeTruePath(String imagePath) {
//         return imageService.getImageTimeStampByTruePath(imagePath);
//     }

//     @Transactional
//     public ArrayList<User> findAllUser() {
//         ArrayList<User> users = new ArrayList<User>();
//         for (User user : userRepository.findAll()) {
//             if (user.getSubscriptionUser() != null) {
//                 if (!LocalDate.now().isBefore(user.getSubscriptionUser().getSubscriptionEnd())) {
//                     detailSubscriptionRepository.deleteById(user.getSubscriptionUser().getId());
//                     user.setSubscriptionUser(null);
//                     userRepository.save(user);
//                 }
//             }
//             if (!user.getIsDeleted())
//                 users.add(user);
//         }
//         return users;
//     }

//     public ArrayList<User> filterUser(FilterBroadcast filter, int days, FilterBroadcast type) {
//         ArrayList<User> result = new ArrayList<User>();
//         if (filter != null) {
//             for (User user : findAllUser()) {
//                 Boolean passFilter = false;
//                 DetailSubscription subsUser = user.getSubscriptionUser();
//                 FilterBroadcast jenisSub = subsUser == null ? FilterBroadcast.NO_SUBSCRIPTION
//                         : (subsUser.getIdPembelian() == null ? FilterBroadcast.TRIAL : FilterBroadcast.PREMIUM);
//                 if (filter.equals(FilterBroadcast.REMAINING_DAYS)) {
//                     if (days > 0 && type != null) {
//                         int sisaHari = Math.toIntExact(Duration.between(LocalDate.now().atStartOfDay(),
//                                 user.getSubscriptionUser().getSubscriptionEnd().atStartOfDay())
//                                 .toDays());
//                         if (sisaHari <= days)
//                             if (type.equals(FilterBroadcast.ALL) || type.equals(jenisSub))
//                                 passFilter = true;
//                     }
//                 } else {
//                     if (filter.equals(FilterBroadcast.ALL) || filter.equals(jenisSub))
//                         passFilter = true;
//                 }
//                 if (passFilter)
//                     result.add(user);
//             }
//         }
//         return result;
//     }

//     public String updateProfilePictureById(String idUser, MultipartFile file, Boolean isCompressing) {
//         Optional<User> optionalUser = findUserById(idUser);
//         if (optionalUser.isPresent()) {
//             User user = optionalUser.get();
//             String imageUrl = imageService.saveImage(file, pathToFoto, false, isCompressing);
//             if (user.getImageUrl() != null) {
//                 imageService.deleteImage(user.getImageUrl());
//             }
//             user.setImageUrl(imageUrl);
//             userRepository.save(user);
//             return imageUrl;
//         }
//         return "";
//     }

//     public boolean deleteProfilePictureById(String idUser) {
//         Optional<User> optionalUser = findUserById(idUser);
//         boolean isDeleted = false;
//         if (optionalUser.isPresent()) {
//             User user = optionalUser.get();
//             if (user.getImageUrl() != null) {
//                 isDeleted = imageService.deleteImage(user.getImageUrl());
//                 if (isDeleted) {
//                     user.setImageUrl(null);
//                     userRepository.save(user);
//                 }
//             }
//         }
//         return isDeleted;
//     }

//     public Map<String, Object> getDetailUserById(String idUser) {
//         User user = findUserById(idUser).get();
//         InformasiUser infoUser = user.getInfoUser();
//         DetailAlamat alamatUser = user.getAlamatUser();
//         DetailSubscription subscriptionUser = user.getSubscriptionUser();

//         return Map.ofEntries(
//                 Map.entry("id", user.getId()),
//                 Map.entry("kodeReferal", Optional.ofNullable(user.getKodeReferal()).orElse("")),
//                 Map.entry("name", user.getNama()),
//                 Map.entry("subdistrict", Optional.ofNullable(alamatUser.getKecamatan()).orElse("")),
//                 Map.entry("village", Optional.ofNullable(alamatUser.getKelurahan()).orElse("")),
//                 Map.entry("email", user.getEmail()),
//                 Map.entry("phoneNumber", user.getNomorTelepon() == null ? "" : user.getNomorTelepon()),
//                 Map.entry("birthDate", infoUser.getTanggalLahir() == null ? "" : infoUser.getTanggalLahir()),
//                 Map.entry("companyName", infoUser.getPerusahaan() == null ? "" : infoUser.getPerusahaan()),
//                 Map.entry("address", alamatUser.getAddress() == null ? "" : alamatUser.getAddress()),
//                 Map.entry("province", alamatUser.getProvinsi() == null ? "" : alamatUser.getProvinsi()),
//                 Map.entry("city",
//                         alamatUser.getKotaKabupaten() == null ? "" : alamatUser.getKotaKabupaten()),
//                 Map.entry("photoUrl", getFoto(user.getId())),
// Map.entry("photoTime", getFotoTime(getFoto(user.getId()))), 
//                 Map.entry("isSubscribed", subscriptionUser != null ? true : false));
//     }

//     public Optional<User> registerAffiliate(BankDTO bankDTO, User user) {
//         BankUser bankUser = user.getBankUser();
//         String kodeReferal = generateReferralCode();
//         if (user.getKodeReferal() == null) {
//             bankUser.setNamaBank(bankDTO.getNamaBank());
//             bankUser.setNomorRekening(bankDTO.getNomorRekening());
//             bankUserRepository.save(bankUser);

//             while (userRepository.existsByKodeReferalAndDeletedAtIsNull(kodeReferal)) {
//                 kodeReferal = generateReferralCode();
//             }
//             user.setKodeReferal(kodeReferal);
//             return Optional.of(userRepository.save(user));
//         }
//         return Optional.empty();
//     }

//     public Optional<User> editBank(BankDTO bankDTO, User user) {
//         BankUser bankUser = user.getBankUser();
//         if (user.getKodeReferal() != null) {
//             bankUser.setNamaBank(bankDTO.getNamaBank());
//             bankUser.setNomorRekening(bankDTO.getNomorRekening());
//             bankUserRepository.save(bankUser);

//             return Optional.of(user);
//         }
//         return Optional.empty();
//     }

//     @Transactional
//     public void setSubscriptionUser(PembelianSubscription pembelian) {
//         User user = pembelian.getIdPenggunaReferal();
//         LocalDate subscriptionEnd = LocalDate.now().plusMonths(pembelian.getLamaSubscription());
//         DetailSubscription subscriptionUser = new DetailSubscription(null, subscriptionEnd, user, pembelian);
//         if (user.getSubscriptionUser() != null) {
//             subscriptionUser = user.getSubscriptionUser();
//             subscriptionUser.setIdPembelian(pembelian);
//             subscriptionUser.setSubscriptionEnd(subscriptionEnd);
//         }
//         detailSubscriptionRepository.save(subscriptionUser);
//     }

//     public Optional<User> findUserByEmail(String email) {
//         List<User> listUser = findAllUser();
//         for (User user : listUser) {
//             if (user.getEmail().equals(email)) {
//                 return Optional.of(user);
//             }
//         }
//         return Optional.empty();
//     }

//     public Optional<User> findUserByRefferal(String refferalCode) {
//         List<User> listUser = findAllUser();
//         for (User user : listUser) {
//             if (Optional.ofNullable(user.getKodeReferal()).map(s -> s.equals(refferalCode)).orElse(false)) {
//                 return Optional.of(user);
//             }
//         }
//         return Optional.empty();
//     }
    
//     public Boolean initializeTestAccount(String email, String password, String name) {
//         if(findUserByEmail(email).isPresent())
//         return false;

//         UserDTO newUser = new UserDTO();
//         newUser.setEmail(email);
//         newUser.setPassword(password);
//         newUser.setNama(name);
//         newUser.setNomorTelepon("08123456789");
//         newUser.setPerusahaan("Test Company");
//         return addUserInstantVerify(newUser).isPresent();
//     }

//     public Optional<User> addUserInstantVerify(UserDTO userDTO) {
//         if (!userRepository.existsByEmailAndDeletedAtIsNull(userDTO.getEmail())) {
//             User newUser = new User(
//                     null, // id
//                     userDTO.getEmail(),
//                     userDTO.getNama(),
//                     passwordMaker.hashPassword(userDTO.getPassword()),
//                     userDTO.getNomorTelepon(),
//                     null,
//                     null,
//                     LocalDateTime.now(),
//                     false,
//                     true,
//                     null, // referal
//                     null, // info
//                     null, // alamat
//                     null, // subs
//                     null // bank
//             );
//             userRepository.save(newUser);

//             InformasiUser infoNewUser = new InformasiUser(
//                     null, // id
//                     userDTO.getPerusahaan(),
//                     userDTO.getTanggalLahir() == null ? null
//                             : LocalDate.parse(userDTO.getTanggalLahir(), formatter), // tanggal lahir
//                     newUser);

//             DetailAlamat alamatNewUser = new DetailAlamat(
//                     null, // id
//                     userDTO.getAlamat(), // alamat
//                     userDTO.getProvinsi(), // provinsi
//                     userDTO.getKota(), // kota
//                     userDTO.getKelurahan(), // kelurahan
//                     userDTO.getKecamatan(), // kecamatan
//                     newUser);

//             DetailSubscription subsUser = new DetailSubscription(
//                     null,
//                     LocalDate.now().plusDays(10),
//                     newUser,
//                     null);

//             BankUser bankUser = new BankUser(
//                     null,
//                     null,
//                     null,
//                     newUser);

//             informasiUserRepository.save(infoNewUser);
//             detailSubscriptionRepository.save(subsUser);
//             detailAlamatRepository.save(alamatNewUser);
//             bankUserRepository.save(bankUser);
//             return Optional.of(newUser);
//         }
//         return Optional.empty();
//     }

//     public Optional<User> addUser(UserDTO userDTO) {
//         if (!userRepository.existsByEmailAndDeletedAtIsNull(userDTO.getEmail())) {
//             User newUser = new User(
//                     null, // id
//                     userDTO.getEmail(),
//                     userDTO.getNama(),
//                     passwordMaker.hashPassword(userDTO.getPassword()),
//                     userDTO.getNomorTelepon(),
//                     null,
//                     null,
//                     LocalDateTime.now(),
//                     false,
//                     false,
//                     null, // referal
//                     null, // info
//                     null, // alamat
//                     null, // subs
//                     null // bank
//             );
//             userRepository.save(newUser);

//             InformasiUser infoNewUser = new InformasiUser(
//                     null, // id
//                     userDTO.getPerusahaan(),
//                     userDTO.getTanggalLahir() == null ? null
//                             : LocalDate.parse(userDTO.getTanggalLahir(), formatter), // tanggal lahir
//                     newUser);

//             DetailAlamat alamatNewUser = new DetailAlamat(
//                     null, // id
//                     userDTO.getAlamat(), // alamat
//                     userDTO.getProvinsi(), // provinsi
//                     userDTO.getKota(), // kota
//                     userDTO.getKelurahan(), // kelurahan
//                     userDTO.getKecamatan(), // kecamatan
//                     newUser);

//             DetailSubscription subsUser = new DetailSubscription(
//                     null,
//                     LocalDate.now().plusDays(10),
//                     newUser,
//                     null);

//             BankUser bankUser = new BankUser(
//                     null,
//                     null,
//                     null,
//                     newUser);

//             informasiUserRepository.save(infoNewUser);
//             detailSubscriptionRepository.save(subsUser);
//             detailAlamatRepository.save(alamatNewUser);
//             bankUserRepository.save(bankUser);
//             return Optional.of(newUser);
//         }
//         return Optional.empty();
//     }

//     public String getSubscriptionById(String idUser) {
//         Optional<User> optionalUser = findUserById(idUser);
//         if (optionalUser.isPresent()) {
//             User user = optionalUser.get();
//             if (user.getSubscriptionUser() != null) {
//                 if (user.getSubscriptionUser().getIdPembelian() == null) {
//                     return "trial";
//                 } else {
//                     return "premium";
//                 }
//             }
//         }
//         return null;
//     }

//     public boolean emailAvailableForEdit(UserDTO userDTO, User user) {
//         return user.getEmail().equalsIgnoreCase(userDTO.getEmail()) ? true
//                 : !userRepository.existsByEmailAndDeletedAtIsNull(userDTO.getEmail());
//     }

//     public boolean emailAvailableForEdit(String email, User user) {
//         return user.getEmail().equalsIgnoreCase(email) ? true
//                 : !userRepository.existsByEmailAndDeletedAtIsNull(email);
//     }

//     public String editEmailByUser(String email, User user) {
//         if (emailAvailableForEdit(email, user)) {
//             user.setEmail(email);
//             userRepository.save(user);
//             return email;
//         }
//         return null;
//     }

//     public User editUser(UserDTO userDTO, User user) {
//         InformasiUser infoUser = user.getInfoUser();
//         DetailAlamat alamatUser = user.getAlamatUser();

//         user.setNama(userDTO.getNama());
//         user.setNomorTelepon(userDTO.getNomorTelepon());

//         infoUser.setPerusahaan(userDTO.getPerusahaan());
//         infoUser.setTanggalLahir(
//                 userDTO.getTanggalLahir() == null ? null : LocalDate.parse(userDTO.getTanggalLahir(), formatter));

//         alamatUser.setAddress(userDTO.getAlamat());
//         alamatUser.setKelurahan(userDTO.getKelurahan());
//         alamatUser.setKecamatan(userDTO.getKecamatan());
//         alamatUser.setProvinsi(userDTO.getProvinsi());
//         alamatUser.setKotaKabupaten(userDTO.getKota());

//         informasiUserRepository.save(infoUser);
//         detailAlamatRepository.save(alamatUser);
//         return user;
//     }

//     public void editPassword(String newPassword, User user) {
//         user.setPassword(passwordMaker.hashPassword(newPassword));
//         userRepository.save(user);
//     }

//     public List<User> getAllUser() {
//         return findAllUser();
//     }

//     public String generateReferralCode() {
//         return RandomStringUtils.randomAlphanumeric(6).toUpperCase();
//     }

//     @Transactional
//     public Optional<User> findUserById(String id) {
//         Optional<User> optionalUser = userRepository.findById(id);
//         if (optionalUser.isPresent()) {
//             User user = optionalUser.get();
//             if (user.getSubscriptionUser() != null) {
//                 if (!LocalDate.now().isBefore(user.getSubscriptionUser().getSubscriptionEnd())) {
//                     detailSubscriptionRepository.deleteById(user.getSubscriptionUser().getId());
//                     user.setSubscriptionUser(null);
//                     userRepository.save(user);
//                 }
//             }
//             if (!user.getIsDeleted())
//                 return Optional.of(user);
//         }
//         return Optional.empty();
//     }

//     public User verifyUser(String id) {
//         User user = findUserById(id).get();
//         user.setIsVerified(true);
//         user.setCreatedAt(LocalDateTime.now());
//         return userRepository.save(user);
//     }

//     public boolean deleteUser(String id) {
//         Optional<User> optionalUser = findUserById(id);
//         if (optionalUser.isPresent()) {
//             User user = optionalUser.get();
//             user.setIsDeleted(true);
//             user.setDeletedAt(LocalDate.now());
//             userRepository.save(user);
//             return true;
//         }
//         return false;
//     }

//     public boolean checkUser(String id) {
//         return findUserById(id).isPresent();
//     }

//     public boolean checkEmailExists(String email) {
//         for (User user : getAllUser()) {
//             if (user.getEmail().equals(email))
//                 return true;
//         }
//         return false;
//     }

// }
