# SOFTENG 325 Assignment 01 - A Concert Booking Service

This application implements the RESTful web service for concert booking. You can create an account, log in, browse concerts, filter concerts, and check seat reservations. When authenticated by logging in and recieving an authentication cookie, you are able to book seats and view all your booked seats. Users may also subscribe to a concert, leading to a notification when the seats are about to sell out.

An OOP domain model created with JPA provides the back end for the web service. JAX-RS is used to implement REST and HTTP methods to provide functionality. Considerations to transactions such that no concerts are double booked and concepts like lazy loading/eager fetching have been made.
