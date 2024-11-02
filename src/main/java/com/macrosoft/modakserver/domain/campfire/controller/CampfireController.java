package com.macrosoft.modakserver.domain.campfire.controller;

import com.macrosoft.modakserver.config.security.CustomUserDetails;
import com.macrosoft.modakserver.domain.campfire.dto.CampfireRequest;
import com.macrosoft.modakserver.domain.campfire.dto.CampfireResponse;
import com.macrosoft.modakserver.domain.campfire.dto.CampfireResponse.CampfirePin;
import com.macrosoft.modakserver.domain.campfire.service.CampfireService;
import com.macrosoft.modakserver.global.BaseResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/campfires")
@Tag(name = "Campfire API", description = "모닥불 관련 API 입니다.")
public class CampfireController {
    private final CampfireService campfireService;

    @Operation(summary = "모닥불 생성", description = "모닥불을 생성합니다.")
    @PostMapping
    public BaseResponse<CampfirePin> createCampfire(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestBody CampfireRequest.CampfireCreate campfireCreate) {
        return BaseResponse.onSuccess(
                campfireService.createCampfire(userDetails.getMember(), campfireCreate.campfireName()));
    }

    @Operation(summary = "내가 참여한 모닥불들의 정보 가져오기", description = "내가 참여한 모든 모닥불의 정보를 가져옵니다.")
    @GetMapping("/my")
    public BaseResponse<CampfireResponse.CampfireInfos> getMyCampfires(
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        return BaseResponse.onSuccess(campfireService.getMyCampfires(userDetails.getMember()));
    }

    @Operation(summary = "모닥불 메인 화면 정보 가져오기", description = "모닥불 id로 이름, 오늘의 사진 정보, 그룹 멤버 id들을 가져옵니다.")
    @GetMapping("/{campfirePin}")
    public BaseResponse<CampfireResponse.CampfireMain> getCampfireMain(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable("campfirePin") int campfirePin) {
        return BaseResponse.onSuccess(campfireService.getCampfireMain(userDetails.getMember(), campfirePin));
    }

    @Operation(summary = "모닥불 이름 가져오기", description = "특정 모닥불의 이름을 가져옵니다.")
    @GetMapping("/{campfirePin}/name")
    public BaseResponse<CampfireResponse.CampfireName> getCampfireName(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable("campfirePin") int campfirePin) {
        return BaseResponse.onSuccess(campfireService.getCampfireName(userDetails.getMember(), campfirePin));
    }

    @Operation(summary = "모닥불 참여", description = "특정 모닥불에 참여합니다.")
    @PostMapping("/{campfirePin}/join")
    public BaseResponse<CampfirePin> joinCampfire(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable("campfirePin") int campfirePin,
            @RequestBody CampfireRequest.CampfireJoin campfireJoin) {
        return BaseResponse.onSuccess(
                campfireService.joinCampfire(userDetails.getMember(), campfirePin, campfireJoin.campfireName()));
    }

    @Operation(summary = "모닥불 이름 변경하기", description = "특정 모닥불의 이름을 변경합니다.")
    @PatchMapping("/{campfirePin}/name")
    public BaseResponse<CampfireResponse.CampfireName> updateCampfireName(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable("campfirePin") int campfirePin,
            @RequestBody CampfireRequest.CampfireUpdateName campfireUpdateName) {
        return BaseResponse.onSuccess(
                campfireService.updateCampfireName(userDetails.getMember(), campfirePin,
                        campfireUpdateName.newCampfireName()));
    }

    @Operation(summary = "모닥불 나가기", description = "특정 모닥불에서 나갑니다.")
    @DeleteMapping("/{campfirePin}/leave")
    public BaseResponse<Void> leaveCampfire(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable("campfirePin") int campfirePin) {
        campfireService.leaveCampfire(userDetails.getMember(), campfirePin);
        return BaseResponse.onSuccess(null);
    }

    @Operation(summary = "모닥불 삭제하기", description = "특정 모닥불을 삭제합니다. 모닥불에 참여한 사용자가 한명일 경우에 가능합니다.")
    @DeleteMapping("/{campfirePin}")
    public BaseResponse<Void> deleteCampfire(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable("campfirePin") int campfirePin) {
        campfireService.deleteCampfire(userDetails.getMember(), campfirePin);
        return BaseResponse.onSuccess(null);
    }
}