# Laporan Praktikum Minggu 3 
Topik: Inheritance (Kategori Produk)

## Identitas
- Nama  : [Lia Lusianti]
- NIM   : [240202869]
- Kelas : [3IKRB]

---

## Tujuan
- Mahasiswa mampu menjelaskan konsep inheritance (pewarisan class) dalam OOP.
- Mahasiswa mampu membuat superclass dan subclass untuk produk pertanian.
- Mahasiswa mampu mendemonstrasikan hierarki class melalui contoh kode.
- Mahasiswa mampu menggunakan super untuk memanggil konstruktor dan method parent class.
- Mahasiswa mampu membuat laporan praktikum yang menjelaskan perbedaan penggunaan inheritance dibanding class tunggal.
---

## Dasar Teori
Inheritance adalah mekanisme dalam OOP yang memungkinkan suatu class mewarisi atribut dan method dari class lain.

- Superclass: class induk yang mendefinisikan atribut umum.
- Subclass: class turunan yang mewarisi atribut/method superclass, dan dapat menambahkan atribut/method baru.
super digunakan untuk memanggil konstruktor atau method superclass.

Dalam konteks Agri-POS, kita dapat membuat class Produk sebagai superclass, kemudian Benih, Pupuk, dan AlatPertanian sebagai subclass. Hal ini membuat kode lebih reusable dan terstruktur.

---

## Langkah Praktikum
1. Membuat Superclass Produk

- Gunakan class Produk dari Bab 2 sebagai superclass.

2. Membuat Subclass

- Benih.java → atribut tambahan: varietas.
- Pupuk.java → atribut tambahan: jenis pupuk (Urea, NPK, dll).
- AlatPertanian.java → atribut tambahan: material (baja, kayu, plastik).

3. Membuat Main Class

- Instansiasi minimal satu objek dari tiap subclass.
- Tampilkan data produk dengan memanfaatkan inheritance.
4. Menambahkan CreditBy

- Panggil class CreditBy untuk menampilkan identitas mahasiswa.
5. Commit dan Push

- Commit dengan pesan: week3-inheritance.

---

## Kode Program

Benih.java
```java
package com.upb.agripos.model;

public class Benih extends Produk {
    private String varietas;

    public Benih(String kode, String nama, double harga, int stok, String varietas) {
        super(kode, nama, harga, stok);
        this.varietas = varietas;
    }

    public String getVarietas() { return varietas; }
    public void setVarietas(String varietas) { this.varietas = varietas; }
}
```

Pupuk.java
```java
package com.upb.agripos.model;

public class Pupuk extends Produk {
    private String jenis;

    public Pupuk(String kode, String nama, double harga, int stok, String jenis) {
        super(kode, nama, harga, stok);
        this.jenis = jenis;
    }

    public String getJenis() { return jenis; }
    public void setJenis(String jenis) { this.jenis = jenis; }
}
```

AlatPertanian.java
```java
package com.upb.agripos.model;

public class AlatPertanian extends Produk {
    private String material;

    public AlatPertanian(String kode, String nama, double harga, int stok, String material) {
        super(kode, nama, harga, stok);
        this.material = material;
    }

    public String getMaterial() { return material; }
    public void setMaterial(String material) { this.material = material; }
}
```
Mainheritance.java
```java
package com.upb.agripos;

import com.upb.agripos.model.*;
import com.upb.agripos.util.CreditBy;

public class MainInheritance {
    public static void main(String[] args) {
        Benih b = new Benih("BNH-001", "Benih Padi IR64", 25000, 100, "IR64");
        Pupuk p = new Pupuk("PPK-101", "Pupuk Urea", 350000, 40, "Urea");
        AlatPertanian a = new AlatPertanian("ALT-501", "Cangkul Baja", 90000, 15, "Baja");

        System.out.println("Benih: " + b.getNama() + " Varietas: " + b.getVarietas());
        System.out.println("Pupuk: " + p.getNama() + " Jenis: " + p.getJenis());
        System.out.println("Alat Pertanian: " + a.getNama() + " Material: " + a.getMaterial());

        CreditBy.print("<NIM>", "<Nama Mahasiswa>");
    }
}
```

---

## Hasil Eksekusi
(Sertakan screenshot hasil eksekusi program.  
![Screenshot hasil](screenshots/hasil_Week3.png)
)
---

## Analisis
- Program menerapkan konsep inheritance, di mana subclass mewarisi atribut dan method dari superclass.
- Superclass berperan sebagai class umum yang menyimpan atribut dasar seperti nama produk, harga, dan stok.
- Subclass digunakan untuk merepresentasikan jenis produk yang lebih spesifik tanpa harus menulis ulang kode yang sama.
- Konstruktor superclass dipanggil menggunakan keyword super agar atribut dasar dapat diinisialisasi dengan benar.
- Pendekatan ini berbeda dengan membuat class terpisah tanpa hubungan, yang berpotensi menyebabkan duplikasi kode.
- Inheritance mempermudah penambahan jenis produk baru pada sistem POS pertanian.
- Kendala yang dihadapi adalah memahami alur pewarisan dan pemanggilan konstruktor superclass.
- Kendala diatasi dengan mempelajari struktur inheritance dan mencoba implementasi sederhana terlebih dahulu.
---

## Kesimpulan
- Inheritance membantu mengurangi duplikasi kode dan meningkatkan efisiensi pengembangan.
- Struktur program menjadi lebih rapi dan mudah dipelihara.
- Penambahan subclass baru dapat dilakukan tanpa mengubah struktur utama program.
- Konsep inheritance sangat mendukung pengembangan aplikasi POS pertanian yang bersifat kompleks dan terus berkembang.
---

## Quiz
1. Apa keuntungan menggunakan inheritance dibanding membuat class terpisah tanpa hubungan?
**Jawaban**: Inheritance memungkinkan reuse kode, mengurangi duplikasi, dan memudahkan pengembangan serta pemeliharaan karena class turunan dapat mewarisi atribut dan method dari class induk.

2. Bagaimana cara subclass memanggil konstruktor superclass?
**Jawaban**: Dengan menggunakan keyword super di dalam konstruktor subclass untuk memanggil konstruktor milik superclass.

3. Contoh kasus subclass di POS pertanian selain Benih, Pupuk, dan Alat Pertanian:
**Jawaban**:
- Pestisida
- Bibit Ternak
- Pakan Ternak
- Obat Tanaman