VLADUT Alexandru-Nicolae
322CA

Proiect POO - Etapa 1

STRUCTURA:

Deserializarea input-ului se realizeaza folosind clasele aferente fisierelor User.java, Movie.java si Action.java.
Output-ul se va realiza folosind obiecte de tipul cu acelasi nume (Output.java).

WORKFLOW:

Folosindu-ne de clasele ObjectMapper si JsonNode extragem input-ul din fisierul .json in 3 categorii,
folosind ArrayList-urile users, movies si actions.

Inainte de a itera prin actiuni, declaram urmatoarele variabile care ne vor ajuta la stocarea datelor pe parcurs:
    outputs - output-urile, care sunt adaugate pe masura ce le generam. Acesta va fi afisat la final in fisierul .out;
    currentPage - numele paginii curente;
    currentUser - informatiile user-ului logat in momentul respectiv;
    currentMovies - lista curenta cu filme, care se modifica in functie de actiunile realizate;

De asemenea, folosim doua HashMap-uri care ne vor ajuta la calculul rating-ului fiecarui film:
    ratingsSum - retine numele filmelor existente drept chei si suma rating-urilor oferite pana acum drept valori;
    ratingsNum - retine numele filmelor existente drept chei si numarul rating-urilot oferite pana acum drept valori;

*Observatii:
    Inregistrarea output-urilor de eroare se va face cu ajutorul functiei errorOutput() din clasa Main.
    Inregistrarea output-urilor de succes se va face cu ajutorul functiei successOutput() din clasa Main.
    La fiecare actiune realizata cu succes actualizam pagina curenta.
    Daca nu ne aflam pe o pagina de pe care putem realiza actiunea => errorOutput() + break.

Iteram prin actiuni:

1. Actiunea nu e nici de tip "change page", nici de tip "on page" => errorOutput()
2. Actiunea e de tip "change page":
    a. "login", "register", "homepage autentificat", "upgrades":
        (Doar ce e in observatii)
    b. "logout":
        - Resetam currentUser.
    c. "movies":
        - Iteram prin movies si verificam daca fiecare film in parte este banat pentru user-ul logat. Daca nu e banat,
        il adaugam in lista resetMovies pe care o vom da la Output.
        - succesOutput()
        - Actualizam currentMovies cu resetMovies pentru actiunile urmatoare.
    d. "see details":
        - Daca filmul dorit nu exista => errorOutput().
        - successOuput(), folosind ca lista de filme doar filmul dorit (seeDetailsMovie).
        - Actualizam currentMovies cu seeDetailsMovie pentru actiunile urmatoare.
3. Actiunea e de tip "on page":
    a. "register":
        - Verificam daca username-ul e deja existent (alreadyExists).
        - Daca username-ul e deja existent => actualizam pagina curenta si errorOutput() + break.
        - Adaugam user-ul in users si actualizam currentUser.
        - succesOutput().
    b. "login":
        - Verificam daca username-ul si parola sunt corecte (authSuccess).
        - Daca sunt incorecte => actualizam pagina curenta si errorOutput() + break.
        - Daca sunt corecte => actualizam currentUser si pagina curenta.
        - succesOutput().
    c. "search":
        - Cream o copie a currentMovies pentru a realiza modificarile asupra ei.
        - Folosim interfata Iterator pentru a elimina filmele care nu incep cu String-ul dat.
        - successOutput(), folosind ca lista de filme copia de mai devreme.
        - NU actualizam currentMovies.
    d. "filter":
        - Cream o copie a currentMovies pentru a realiza modificarile asupra ei.
        - Daca exista campul "contains", eliminam (folosind Iterator) filmele care nu au actorii si genurile dorite.
        - Daca exista campul "sort", ordonam filmele din lista conform criteriilor "duration" si "rating".
        Pentru fiecare combinatie posibila folosim o clasa specifica care implementeaza interfata Comparator.]
        Aceste clase sunt declarate la finalul fisierului Main.java.
        - successOutput(), folosind ca lista de filme copia de mai devreme.
        - Actualizam currentMovies cu copia anterior realizata pentru actiunile urmatoare.
    e. "buy tokens":
        - Cream o copie a currentUser pentru a realiza modificarile asupra ei.
        - Daca user-ul nu are campul "balance" suficient pentru achizitie => errorOutput().
        - Ajustam campurile "balance" si "tokensCount" in urma achizitiei.
        - Actualizam currentUser cu copia anterior realizata pentru actiunile urmatoare.
    f. "buy premium account":
        - Daca user-ul e deja premium => errorOutput().
        - Cream o copie a currentUser pentru a realiza modificarile asupra ei.
        - Daca user-ul nu are campul "balance" suficient pentru achizitie => errorOutput().
        - Ajustam campurile "balance" si "accountType" in urma achizitiei.
        - Actualizam currentUser cu copia anterior realizata pentru actiunile urmatoare.
    g. "purchase":
        - Daca filmul dorit nu exista sau e deja cumparat => errorOutput().
        - Cream o copie a currentUser pentru a realiza modificarile asupra ei.
        - Daca user-ul e de tip "standard":
            - Daca user-ul nu are campul "balance" suficient pentru achizitie => errorOutput().
            - Ajustam campurile "tokensCount" si "purchasedMovies" in urma achizitiei.
            - succesOutput().
            - Actualizam currentUser cu copia anterior realizata pentru actiunile urmatoare.
        - Daca user-ul e de tip "standard":
            - Daca user-ul nu are campurile "numFreePremiumMovies" si "balance" suficient
            pentru achizitie => errorOutput().
            - Ajustam campurile "numFreePremiumMovies"/"tokensCount" (dupa caz) si "purchasedMovies"
            in urma achizitiei.
            - succesOutput().
            - Actualizam currentUser cu copia anterior realizata pentru actiunile urmatoare.
    h. "watch", "like" si "rate":
        - Principiu similar cu "purchase".
        - "like" si "rate" modifica campuri ale mai multor liste (se observa mai multe for-uri consecutive).