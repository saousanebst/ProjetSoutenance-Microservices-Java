package fr.projet.api;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import fr.projet.model.Compte;
import fr.projet.repository.CompteRepository;
import fr.projet.request.CreateCompteRequest;
import fr.projet.response.CompteResponse;
import fr.projet.service.CompteSrv;

@RestController
@RequestMapping("/api/compte")
@CrossOrigin("*")

public class CompteApiController {
    
@Autowired
private CompteRepository compteRepository;
@Autowired
private CompteSrv compteService;


//findAll
@GetMapping
    public List<CompteResponse> findAll() {
        List<Compte> comptes = this.compteRepository.findAll();
        List<CompteResponse> response = new ArrayList<>();

        for (Compte compte : comptes) {
            CompteResponse compteResponse = new CompteResponse();

            BeanUtils.copyProperties(compte, compteResponse);

            response.add(compteResponse);

            }
        
        
        return response;
    }

//findAllbyidfacultatifs


     @GetMapping("/{id}")
    public CompteResponse findById(@PathVariable String id) {
        Compte compte = compteService.findById(id);
        CompteResponse compteResponse = new CompteResponse();
        BeanUtils.copyProperties(compte, compteResponse);
        return compteResponse;
    }



//Create

@PostMapping("/ajout")
    @ResponseStatus(HttpStatus.CREATED)
    public String create(@RequestBody CreateCompteRequest request) {
        Compte  compte= new Compte();
        
        BeanUtils.copyProperties(request, compte);

        this.compteRepository.save(compte);

        return compte.getId();
    }

//update

@PutMapping("/{id}")
	public Compte updateCompte (@PathVariable String id,@RequestBody Compte compte) 
	{
		compte.setId(id);
		return compteService.update(compte);
	}
	
//delete	
	@DeleteMapping("/{id}")
	public void deleteById(@PathVariable String id) 
	{
		compteService.deleteCompteById(id);
	}


}


























