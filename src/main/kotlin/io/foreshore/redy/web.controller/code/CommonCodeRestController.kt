package io.foreshore.redy.web.controller.code

import com.obvioustraverse.miska.util.api.ApiReturn
import io.foreshore.redy.service.code.CommonCodeDto
import io.foreshore.redy.service.code.CommonCodeService
import io.foreshore.redy.service.code.dto
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Pageable
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*
import javax.validation.Valid
import javax.validation.constraints.Size

@RestController
@RequestMapping("/ajax/code")
class CommonCodeRestController(
    @Autowired private val commonCodeService: CommonCodeService
) {

    @PreAuthorize("hasAuthority('SYS_CODE')")
    @GetMapping("/listAll")
    fun listAll(@Valid @Size(max = 200) @RequestParam("search") search: String?, pageable: Pageable) =
        ApiReturn.of(commonCodeService.findAll(search, pageable))

    @PreAuthorize("hasAuthority('SYS_CODE')")
    /** enabled only */
    @GetMapping("/group/list")
    fun listEnabledGroups() = ApiReturn.of(commonCodeService.findAllEnabledGroups())

    @PreAuthorize("hasAuthority('SYS_CODE')")
    /** all */
    @GetMapping("/group/listAll")
    fun listAllGroups() = ApiReturn.of(commonCodeService.findAllGroups())

    @GetMapping("/list/{parentCode}")
    fun listEnabledByParent(@PathVariable("parentCode") parentCode: String) =
        ApiReturn.of(commonCodeService.findAllEnabledByParentCode(parentCode))

    @GetMapping("/listAll/{parentCode}")
    fun listByParent(@PathVariable("parentCode") parentCode: String) =
        ApiReturn.of(commonCodeService.findAllByParentCode(parentCode))

    @GetMapping("/get/{code}")
    fun get(@PathVariable("code") code: String) = ApiReturn.of(CommonCodeDto(commonCodeService.findByIdOrElseThrow(code)))

    /** axios 는 array param 에 [] 를 붙여준다 */
    @GetMapping("/listMulti")
    fun listMulti(@RequestParam("parentCodes[]") parentCodes: Array<String>) = ApiReturn.of(
        mutableMapOf<String, List<CommonCodeDto>>().apply {
            parentCodes.forEach {
                this[it] = commonCodeService.findAllEnabledByParentCode(it)
            }
        })

    @PreAuthorize("hasAuthority('SYS_CODE')")
    @PostMapping("/")
    fun create(@RequestBody @Valid request: CommonCodeDto) = ApiReturn.of(commonCodeService.create(request).dto)

    @PreAuthorize("hasAuthority('SYS_CODE')")
    @PutMapping("/{code}")
    fun update(@PathVariable("code") code: String, @RequestBody @Valid request: CommonCodeDto) =
        ApiReturn.of(commonCodeService.update(code, request).dto)

    @PreAuthorize("hasAuthority('SYS_CODE')")
    @DeleteMapping("/{code}")
    fun delete(@PathVariable("code") code: String) = ApiReturn.of(commonCodeService.deleteById(code))
}
