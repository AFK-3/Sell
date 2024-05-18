### Sell
#### Proof of Monitoring
Proof of monitoring online :
![image](https://github.com/AFK-3/Sell/assets/119410845/d443ab84-657b-4e58-b45e-158192650cb5)

Result dari /actuator/prometheus pada lokal
![image](https://github.com/AFK-3/Sell/assets/119410845/2ebad7f0-3a7d-4aab-bc95-346b26a20ec4)

Dashboard Grafana pada lokal
![image](https://github.com/AFK-3/Sell/assets/119410845/adae9a85-1da0-4634-aefe-bc81374894a8)

Prometheus pada lokal
![image](https://github.com/AFK-3/Sell/assets/119410845/3fda360d-d1a1-4cb1-91a3-c613f98e5ccb)

#### Proof of Profiling

Berikut salah satu contoh dari hasil refactoring saya untuk mempercepat pengiriman response dari server. Dapat dilihat pada /get-all dibutuhkan waktu 1000 ms-an. Hal ini saya asumsikan terjadi karena diperlukannya pembuatan request terhadap service auth untuk mendapatkan informasi mengenai username dari JWT. Oleh karena itu, dibuatlah suatu class khusus bernama JWTValidator yang berfungs sebagai parser JWT menjadi username sehingga mengirit waktu yang diperlukannya.
![image](https://github.com/AFK-3/Sell/assets/119410845/5df1cabd-f68e-4f58-8f9b-794c259a3b5d)

Hasilnya adalah sebagai berikut
![image](https://github.com/AFK-3/Sell/assets/119410845/426ab910-93d4-4e6a-b0e2-52a0b4849160)

Hal yang sama juga dilakukan untuk query-query lain sehingga menjadi lebih cepat.

Berikut adalah hasil terakhir flame chart dari Profilingnya
![image](https://github.com/AFK-3/Sell/assets/119410845/2e0ea309-a494-48eb-baad-f06e043e1b44)
