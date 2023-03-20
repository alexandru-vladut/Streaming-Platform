package io.users;

public class Credentials {
    private String name;
    private String password;
    private String accountType;
    private String country;
    private String balance;

    public Credentials(final String name, final String password, final String accountType,
                final String country, final String balance) {
        this.name = name;
        this.password = password;
        this.accountType = accountType;
        this.country = country;
        this.balance = balance;
    }

    public Credentials(final Credentials credentials) {
        this.name = credentials.name;
        this.password = credentials.password;
        this.accountType = credentials.accountType;
        this.country = credentials.country;
        this.balance = credentials.balance;
    }

    public Credentials() { }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(final String password) {
        this.password = password;
    }

    public String getAccountType() {
        return accountType;
    }

    public void setAccountType(final String accountType) {
        this.accountType = accountType;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(final String country) {
        this.country = country;
    }

    public String getBalance() {
        return balance;
    }

    public void setBalance(final String balance) {
        this.balance = balance;
    }
}
