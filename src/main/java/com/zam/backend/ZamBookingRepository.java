package com.zam.backend;

import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface ZamBookingRepository extends CrudRepository<ZamBooking, Integer> {
    List<ZamBooking> findZamBookingByIdAsset(ZamAsset idAsset);

    void removeZamBookingByIdUtente(ZamUser idUtente);

    List<ZamBooking> findByIdUtente(ZamUser idUtente);
}
