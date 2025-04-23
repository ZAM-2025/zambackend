package com.zam.backend;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@CrossOrigin(origins = "*")
public class WebAssetController {
    private final ZamAssetRepository assetRepository;
    private final ZamUserRepository zamUserRepository;
    private final ZamTokenRepository tokenRepository;

    @PostMapping(value = "/api/assets/all", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Iterable<ZamAsset> getAllAssets() {
        return assetRepository.findAll();
    }

    @PostMapping(value = "/api/assets/active", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Iterable<ZamAsset> getActiveAssets() {
        return assetRepository.findByAttivo(true);
    }

    @PostMapping(value = "/api/assets/floor", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Iterable<ZamAsset> getFloorAssets(@RequestBody ZamFloorRequest floor) {
        ZamLogger.log("Loading floor " + floor.floor());
        return assetRepository.findByPiano(floor.floor());
    }

    @PostMapping(value = "/api/assets/floor-active", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Iterable<ZamAsset> getActiveFloorAssets(@RequestBody ZamFloorRequest floor) {
        ZamLogger.log("Loading floor " + floor.floor());
        return assetRepository.findByPianoAndAttivo(floor.floor(), true);
    }

    @PostMapping(value = "/api/assets/disable", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public WebGenericResponse disableAsset(@RequestBody WebAssetStatusRequest request) {
        ZamUser user = this.tokenRepository.findUser(request.token());

        if(user == null) {
            return new WebGenericResponse(false, "No such user");
        }

        if(user.getTipo() != ZamUserType.GESTORE) {
            return new WebGenericResponse(false, "Not allowed");
        }

        Optional<ZamAsset> asset = this.assetRepository.findById(request.assetID());
        if(asset.isEmpty()) {
            return new WebGenericResponse(false, "No such asset");
        }

        ZamAsset _asset = asset.get();
        _asset.setActive(false);

        this.assetRepository.save(_asset);

        return new WebGenericResponse(true, "Asset disabled");
    }

    @PostMapping(value = "/api/assets/enable", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public WebGenericResponse enableAsset(@RequestBody WebAssetStatusRequest request) {
        ZamUser user = this.tokenRepository.findUser(request.token());

        if(user == null) {
            return new WebGenericResponse(false, "No such user");
        }

        if(user.getTipo() != ZamUserType.GESTORE) {
            return new WebGenericResponse(false, "Not allowed");
        }

        Optional<ZamAsset> asset = this.assetRepository.findById(request.assetID());
        if(asset.isEmpty()) {
            return new WebGenericResponse(false, "No such asset");
        }

        ZamAsset _asset = asset.get();
        _asset.setActive(true);

        this.assetRepository.save(_asset);

        return new WebGenericResponse(true, "Asset enabled");
    }

    public WebAssetController(ZamAssetRepository assetRepository, ZamUserRepository zamUserRepository, ZamTokenRepository tokenRepository) {
        this.assetRepository = assetRepository;
        this.zamUserRepository = zamUserRepository;
        this.tokenRepository = tokenRepository;
    }
}
