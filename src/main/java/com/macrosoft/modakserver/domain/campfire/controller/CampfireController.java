package com.macrosoft.modakserver.domain.campfire.controller;

import com.macrosoft.modakserver.config.security.CustomUserDetails;
import com.macrosoft.modakserver.domain.campfire.dto.CampfireRequest;
import com.macrosoft.modakserver.domain.campfire.dto.CampfireResponse;
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
    public BaseResponse<CampfireResponse.CampfireId> createCampfire(
            @RequestBody CampfireRequest.CampfireCreate campfireCreate) {
        return BaseResponse.onSuccess(campfireService.createCampfire(campfireCreate.campfireName()));
    }

    @Operation(summary = "내가 참여한 모닥불 정보 가져오기", description = "내가 참여한 모든 모닥불의 정보를 가져옵니다.")
    @GetMapping("/my")
    public BaseResponse<CampfireResponse.CampfireInfos> getMyCampfires() {
        return BaseResponse.onSuccess(campfireService.getMyCampfires());
    }

    @Operation(summary = "모닥불 메인 화면 정보 가져오기", description = "모닥불 id로 이름, 오늘의 사진 정보, 그룹 멤버 id들을 가져온다")
    @GetMapping("/{campfireId}")
    public BaseResponse<CampfireResponse.CampfireMain> getCampfireMain(@PathVariable int campfireId) {
        return BaseResponse.onSuccess(campfireService.getCampfireMain(campfireId));
    }

    @Operation(summary = "모닥불 이름 가져오기", description = "특정 모닥불의 이름을 가져옵니다.")
    @GetMapping("/{campfireId}/inviteInfo")
    public BaseResponse<CampfireResponse.CampfireName> getCampfireName(@PathVariable int campfireId) {
        return BaseResponse.onSuccess(campfireService.getCampfireInvitations(campfireId));
    }

    @Operation(summary = "모닥불 참여", description = "특정 모닥불에 참여합니다.")
    @PostMapping("/{campfireId}/join")
    public BaseResponse<CampfireResponse.CampfireId> joinCampfire(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable int campfireId,
            @RequestBody CampfireRequest.CampfireJoin campfireJoin) {
        return BaseResponse.onSuccess(
                campfireService.joinCampfire(userDetails.getMember(), campfireId, campfireJoin.campfireName()));
    }

    @Operation(summary = "모닥불 이름 변경하기", description = "특정 모닥불의 이름을 변경합니다.")
    @PatchMapping("/{campfireId}/name")
    public BaseResponse<CampfireResponse.CampfireName> updateCampfireName(
            @PathVariable int campfireId,
            @RequestBody CampfireRequest.CampfireUpdateName campfireUpdateName) {
        return BaseResponse.onSuccess(
                campfireService.updateCampfireName(campfireId, campfireUpdateName.newCampfireName()));
    }

    @Operation(summary = "모닥불 삭제하기", description = "특정 모닥불을 삭제합니다. 모닥불에 참여한 사용자가 한명일 경우에 가능합니다.")
    @DeleteMapping("/{campfireId}")
    public BaseResponse<CampfireResponse.CampfireId> deleteCampfire(@PathVariable int campfireId) {
        return BaseResponse.onSuccess(campfireService.deleteCampfire(campfireId));
    }
}
