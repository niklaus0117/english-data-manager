package com.englishdatamanager.backend.controller.admin;

import com.englishdatamanager.backend.common.ApiResponse;
import com.englishdatamanager.backend.entity.VideoAlbum;
import com.englishdatamanager.backend.entity.VideoAlbumItem;
import com.englishdatamanager.backend.service.VideoAlbumItemService;
import com.englishdatamanager.backend.service.VideoAlbumService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/admin/albums")
@RequiredArgsConstructor
public class AdminAlbumController {

    private final VideoAlbumService videoAlbumService;
    private final VideoAlbumItemService videoAlbumItemService;

    @GetMapping
    public ApiResponse<List<VideoAlbum>> list(@RequestParam(required = false) Long categoryId,
                                              @RequestParam(required = false) Integer status) {
        return ApiResponse.success(videoAlbumService.lambdaQuery()
                .eq(categoryId != null, VideoAlbum::getCategoryId, categoryId)
                .eq(status != null, VideoAlbum::getStatus, status)
                .orderByDesc(VideoAlbum::getIsFeatured)
                .orderByAsc(VideoAlbum::getSortNo)
                .orderByDesc(VideoAlbum::getId)
                .list());
    }

    @GetMapping("/{id}")
    public ApiResponse<VideoAlbum> detail(@PathVariable Long id) {
        return ApiResponse.success(videoAlbumService.getById(id));
    }

    @PostMapping
    public ApiResponse<Void> create(@RequestBody VideoAlbum album) {
        videoAlbumService.save(album);
        return ApiResponse.success();
    }

    @PutMapping("/{id}")
    public ApiResponse<Void> update(@PathVariable Long id, @RequestBody VideoAlbum album) {
        album.setId(id);
        videoAlbumService.updateById(album);
        return ApiResponse.success();
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        videoAlbumService.removeById(id);
        videoAlbumItemService.lambdaUpdate()
                .eq(VideoAlbumItem::getAlbumId, id)
                .remove();
        return ApiResponse.success();
    }

    @GetMapping("/{albumId}/items")
    public ApiResponse<List<VideoAlbumItem>> itemList(@PathVariable Long albumId) {
        return ApiResponse.success(videoAlbumItemService.lambdaQuery()
                .eq(VideoAlbumItem::getAlbumId, albumId)
                .orderByAsc(VideoAlbumItem::getSortNo)
                .orderByAsc(VideoAlbumItem::getId)
                .list());
    }

    @PostMapping("/{albumId}/items")
    public ApiResponse<Void> createItem(@PathVariable Long albumId, @RequestBody VideoAlbumItem item) {
        item.setAlbumId(albumId);
        videoAlbumItemService.save(item);
        return ApiResponse.success();
    }

    @PutMapping("/items/{id}")
    public ApiResponse<Void> updateItem(@PathVariable Long id, @RequestBody VideoAlbumItem item) {
        item.setId(id);
        videoAlbumItemService.updateById(item);
        return ApiResponse.success();
    }

    @DeleteMapping("/items/{id}")
    public ApiResponse<Void> deleteItem(@PathVariable Long id) {
        videoAlbumItemService.removeById(id);
        return ApiResponse.success();
    }
}
