### Sell
### Proof of Profiling

Berikut salah satu contoh dari hasil refactoring saya untuk mempercepat pengiriman response dari server. Dapat dilihat pada /get-all dibutuhkan waktu 1000 ms-an. Hal ini saya asumsikan terjadi karena diperlukannya pembuatan request terhadap service auth untuk mendapatkan informasi mengenai username dari JWT. Oleh karena itu, dibuatlah suatu class khusus bernama JWTValidator yang berfungs sebagai parser JWT menjadi username sehingga mengirit waktu yang diperlukannya.
![image](https://github.com/AFK-3/Sell/assets/119410845/5df1cabd-f68e-4f58-8f9b-794c259a3b5d)

Hasilnya adalah sebagai berikut
![image](https://github.com/AFK-3/Sell/assets/119410845/426ab910-93d4-4e6a-b0e2-52a0b4849160)

Hal yang sama juga dilakukan untuk query-query lain sehingga menjadi lebih cepat.

Berikut adalah hasil terakhir flame chart dari Profilingnya
![image](https://github.com/AFK-3/Sell/assets/119410845/2e0ea309-a494-48eb-baad-f06e043e1b44)
