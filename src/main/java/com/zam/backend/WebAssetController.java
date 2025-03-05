package com.zam.backend;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin(origins = "*")
public class WebAssetController {
    private final ZamAssetRepository assetRepository;

    @PostMapping(value = "/api/assets/all", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Iterable<ZamAsset> getAllAssets() {
        return assetRepository.findAll();
    }

    @PostMapping(value = "/api/assets/floor", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Iterable<ZamAsset> getFloorAssets(@RequestBody ZamFloorRequest floor) {
        ZamLogger.log("Loading floor " + floor.floor());
        return assetRepository.findByPiano(floor.floor());
    }

    public WebAssetController(ZamAssetRepository assetRepository) {
        this.assetRepository = assetRepository;
    }
}
