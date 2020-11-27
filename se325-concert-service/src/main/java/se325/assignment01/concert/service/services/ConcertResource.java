package se325.assignment01.concert.service.services;


import se325.assignment01.concert.common.dto.*;
import se325.assignment01.concert.common.types.BookingStatus;
import se325.assignment01.concert.service.domain.*;
import se325.assignment01.concert.service.jaxrs.LocalDateTimeParam;
import se325.assignment01.concert.service.mapper.*;

import javax.persistence.*;
import javax.ws.rs.*;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.container.Suspended;
import javax.ws.rs.core.*;
import javax.ws.rs.core.Response.ResponseBuilder;
import java.net.URI;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Path("/concert-service")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ConcertResource {

    /**
     * Returns a concert, represented by a concert object.
     *
     * @param id the unique id of the concert
     */
    @GET
    @Path("/concerts/{id}")
    public Response getConcert(@PathParam("id") long id) {
        EntityManager em = PersistenceManager.instance().createEntityManager();

        try {
            em.getTransaction().begin();
            Concert concert = em.find(Concert.class, id);


            if (concert == null){
                return Response.status(Response.Status.NOT_FOUND).build();
            }
            ConcertDTO concertDTO = ConcertMapper.toDto(concert);
            em.getTransaction().commit();
            ResponseBuilder builder = Response.ok(concertDTO);

            return builder.build();

        } finally {
            em.close();
        }

    }

    /**
     * Returns a list of all concerts in the system.
     *
     * @return GenericType<List<ConcertDTO>>
     */
    @GET
    @Path("/concerts")
    public Response getAllConcerts(){
        List<ConcertDTO> allConcerts = new ArrayList<>();
        EntityManager em = PersistenceManager.instance().createEntityManager();
        try {
            ///em.getTransaction().begin();

            TypedQuery<Concert> concertQuery = em.createQuery("select c from Concert c", Concert.class);
            List<Concert> concerts = concertQuery.getResultList();

            for (Concert c : concerts) {
                allConcerts.add(ConcertMapper.toDto(c));
            }

            //em.getTransaction().commit();

            ResponseBuilder builder = Response.ok(allConcerts);
            return builder.build();

        } finally{
            em.close();
        }
    }

    /**
     * Returns a list of all concerts in the system, and returns the summaries.
     *
     * @return GenericType<List<ConcertSummaryDTO>>
     */
    @GET
    @Path("/concerts/summaries")
    public Response getConcertSummaries(){
        List<ConcertSummaryDTO> allConcerts = new ArrayList<>();
        EntityManager em = PersistenceManager.instance().createEntityManager();
        try {
            em.getTransaction().begin();

            TypedQuery<Concert> concertQuery = em.createQuery("select c from Concert c", Concert.class);
            List<Concert> concerts = concertQuery.getResultList();

            for (Concert c : concerts) {
                allConcerts.add(ConcertMapper.toSummaryDto(c));
            }

            em.getTransaction().commit();

            ResponseBuilder builder = Response.ok(allConcerts);
            return builder.build();

        } finally{
            em.close();
        }
    }

    /**
     * Returns a specific performer, indexed by its unique ID.
     *
     * @return PerformerDTO
     */
    @GET
    @Path("/performers/{id}")
    public Response getPerformer(@PathParam("id") long id){

        EntityManager em = PersistenceManager.instance().createEntityManager();

        try {
            em.getTransaction().begin();
            Performer performer = em.find(Performer.class, id);
            em.getTransaction().commit();

            if (performer == null){
                return Response.status(Response.Status.NOT_FOUND).build();
                ///throw new WebApplicationException(Response.Status.NOT_FOUND);
            }
            PerformerDTO performerDTO = PerformerMapper.toDto(performer);
            ResponseBuilder builder = Response.ok(performerDTO);

            return builder.build();

        } finally {
            em.close();
        }
    }

    /**
     * Returns a list of all performers in the system.
     *
     * @return GenericType<List<PerformerDTO>>
     */
    @GET
    @Path("/performers")
    public Response getAllPerformers(){
        List<PerformerDTO> allPerformers = new ArrayList<>();
        EntityManager em = PersistenceManager.instance().createEntityManager();
        try {
            em.getTransaction().begin();

            TypedQuery<Performer> Query = em.createQuery("select p from Performer p", Performer.class);
            List<Performer> performers = Query.getResultList();

            for (Performer p : performers) {
                allPerformers.add(PerformerMapper.toDto(p));
            }

            em.getTransaction().commit();

            ResponseBuilder builder = Response.ok(allPerformers);
            return builder.build();

        } finally{
            em.close();
        }
    }

    /**
     * Allows a user to login by checking sent credentials against each user in the database.
     * Returns UNAUTHORIZED if the user is not logged in, and ok if the login was found.
     * The authorized cookie returned is saved to the user in the database,
     * and used to authorize for other tasks by comparing the sent cookie and the user's saved cookie.
     * @return Cookie
     */
    @POST
    @Path("/login")
    public Response doLogin(UserDTO userDTO){

        EntityManager em = PersistenceManager.instance().createEntityManager();
        User user = UserMapper.toDomainModel(userDTO);
        String sentUsername = user.getUsername();
        String sentPassword = user.getPassword();
        try {

            em.getTransaction().begin();

            TypedQuery<User> userQuery = em.createQuery("select u from User u where u.username = :uname " +
                    "and u.password = :pword", User.class);
            userQuery.setParameter("uname", sentUsername);
            userQuery.setParameter("pword", sentPassword);
            User foundUser = userQuery.getSingleResult();

            NewCookie newCookie = new NewCookie("auth",UUID.randomUUID().toString());

            ResponseBuilder builder = Response.ok(UserMapper.toDto(foundUser));
            builder.cookie(newCookie);
            foundUser.setAuthCookie(newCookie.getValue());
            em.getTransaction().commit();
            return builder.build();

        } catch (NoResultException e) {
            //em.getTransaction().commit();
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }

        finally {
            em.close();
        }
    }


    /**
     * Creates a new booking based on a BookingRequestDTO, provided the user is logged in,
     * and the concert/date/login is valid.
     *
     * Returns a link to the newly created booking.
     */
    @POST
    @Path("/bookings")
    public Response makeBooking(BookingRequestDTO bookingRequestDTO, @CookieParam("auth") Cookie authCookie){

        EntityManager em = PersistenceManager.instance().createEntityManager();

        try {
            if (authCookie == null) {
                return Response.status(Response.Status.UNAUTHORIZED).build();
            }
            User user = readAuthCookie(authCookie);
            em.getTransaction().begin();
            TypedQuery<Concert> concertQuery = em.createQuery("select c from Concert c where " +
                    "c.id =: cid and : cdates member of c.dates",Concert.class);
            concertQuery.setParameter("cid",bookingRequestDTO.getConcertId());
            concertQuery.setParameter("cdates",bookingRequestDTO.getDate());
            Concert bookedConcert = concertQuery.getSingleResult();

            LocalDateTime bookingDate = bookingRequestDTO.getDate();
            List<String> pendingSeats = bookingRequestDTO.getSeatLabels();
            TypedQuery<Seat> query = em.createQuery(
                    "select s from Seat s where s.date =: bookingLDT " +
                            "AND s.label IN :labelList",
                    Seat.class);
            query.setParameter("bookingLDT",bookingDate);
            query.setParameter("labelList",pendingSeats);
            List<Seat> requestedSeats = query.getResultList();

            //find seats by date and book

            // check all seats are valid
            for (Seat s: requestedSeats){
                if (s.isBooked()) {
                    return Response.status(Response.Status.FORBIDDEN).build();
                }

            }

            for (Seat s: requestedSeats){
                s.setBooked(true);
            }

            Booking booking = new Booking(bookingRequestDTO.getConcertId(), bookingDate,requestedSeats);
            booking.setUser(user);
            user.addBooking(booking);


            em.persist(booking);
            em.getTransaction().commit();

            // return created response and created bookingDTO
            return Response.created(URI.create("/concert-service/bookings/" + booking.getId())).build();

        } catch(NoResultException e) {
           // em.getTransaction().commit();
            return Response.status(Response.Status.BAD_REQUEST).build();
        } finally {
            em.close();
        }
    }


    /**
     * Returns a list of all seats for a specific date, and for a specific BookingStatus.
     *
     * @return List<Seat>
     */
    @GET
    @Path("/seats/{LDT}")
    public Response getSeats(@PathParam("LDT") LocalDateTimeParam LDT,
                             @QueryParam("status") BookingStatus status){

        EntityManager em = PersistenceManager.instance().createEntityManager();

        try{

            LocalDateTime date = LDT.getLocalDateTime();
            em.getTransaction().begin();
            boolean boolStatus;
            List<Seat> foundSeats;

            if (status == BookingStatus.Any) {

                TypedQuery<Seat> query = em.createQuery(
                        "select s from Seat s where s.date =: date ", Seat.class);
                query.setParameter("date",date);
                foundSeats = query.getResultList();

            } else {

                boolStatus = status == BookingStatus.Booked;

                TypedQuery<Seat> query = em.createQuery(
                        "select s from Seat s where s.date =: date " +
                                "AND s.isBooked =: boolStatus", Seat.class);
                query.setParameter("date",date);
                query.setParameter("boolStatus",boolStatus);
                foundSeats = query.getResultList();
            }

            em.getTransaction().commit();

            ResponseBuilder builder = Response.ok(foundSeats);
            return builder.build();

        } finally {

            em.close();
        }
    }


    /**
     * Returns a specific booking, referenced by ID.
     *
     * @return BookingDTO
     */
    @GET
    @Path("/bookings/{id}")
    public Response getBooking(@PathParam("id") long id, @CookieParam("auth") Cookie authCookie){

        EntityManager em = PersistenceManager.instance().createEntityManager();

        try {
            em.getTransaction().begin();
            Booking booking = em.find(Booking.class, id);
            em.getTransaction().commit();

            if (booking == null){
                return Response.status(Response.Status.NOT_FOUND).build();
                ///throw new WebApplicationException(Response.Status.NOT_FOUND);
            }

            User authUser = readAuthCookie(authCookie);

            if (!authUser.getAuthCookie().equals(booking.getUser().getAuthCookie())){
                return Response.status(Response.Status.FORBIDDEN).build();
            }

            BookingDTO bookingDTO = BookingMapper.toDto(booking);
            ResponseBuilder builder = Response.ok(bookingDTO);

            return builder.build();

        } finally {
            em.close();
        }
    }


    /**
     * Returns a list of all of a user's bookings.
     *
     * @return List<BookingDTO>
     */
    @GET
    @Path("/bookings/")
    public Response getUserBookings(@CookieParam("auth") Cookie authCookie){
        EntityManager em = PersistenceManager.instance().createEntityManager();
        try {
            User authUser = readAuthCookie(authCookie);
            List<BookingDTO> userBookings = new ArrayList<>();

            if (authUser == null){
                return Response.status(Response.Status.UNAUTHORIZED).build();
            }

//            if (authUser.getBookings().size() == 0){
//                return Response.status(Response.Status.NO_CONTENT).build();
//
//            }
            em.getTransaction().begin();

            for (Booking b: authUser.getBookings()){
                userBookings.add(BookingMapper.toDto(b));
            }
            em.getTransaction().commit();
            ResponseBuilder builder = Response.ok(userBookings);
            return builder.build();

        } finally {
            em.close();
        }
    }

    @POST
    @Path("/subscribe/concertInfo")
    public Response subscribeToConcert(ConcertInfoSubscriptionDTO infoSubscription, @Suspended AsyncResponse sub,
                                       @CookieParam("auth") Cookie authCookie){
        EntityManager em = PersistenceManager.instance().createEntityManager();

        try {

            if (authCookie == null) {
                sub.resume(Response.status(Response.Status.UNAUTHORIZED));
                return Response.status(Response.Status.UNAUTHORIZED).build();
            }

            User user = readAuthCookie(authCookie);
            em.getTransaction().begin();
            TypedQuery<Concert> concertQuery = em.createQuery("select c from Concert c where " +
                    "c.id =: cid and : cdates member of c.dates",Concert.class);
            concertQuery.setParameter("cid",infoSubscription.getConcertId());
            concertQuery.setParameter("cdates",infoSubscription.getDate());
            Concert bookedConcert = concertQuery.getSingleResult();

            return Response.status(Response.Status.NOT_IMPLEMENTED).build();
        } catch(NoResultException e) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        } finally {
            em.close();
        }

    }

    //helper function to read cookies
    public User readAuthCookie(Cookie authCookie){
        EntityManager em2 = PersistenceManager.instance().createEntityManager();
        try {
            em2.getTransaction().begin();
            if (authCookie == null){
                return null;
            }
            TypedQuery<User> query = em2.createQuery("select u from User u where u.authCookie =: auth", User.class)
                    .setParameter("auth", authCookie.getValue());

            User foundUser = query.getSingleResult();
            em2.getTransaction().commit();

            return foundUser;
        } catch(NoResultException e){

            return null;

        } finally {
            em2.close();
        }
    }

}
