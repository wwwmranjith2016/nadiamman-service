package com.billflow.service;

import com.billflow.model.Client;
import com.billflow.repository.ClientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ClientService {
    
    private final ClientRepository clientRepository;
    
    public List<Client> getAllClients() {
        return clientRepository.findAll();
    }
    
    public Client getClientById(Long id) {
        return clientRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Client not found with id: " + id));
    }
    
    @Transactional
    public Client createClient(Client client) {
        if (clientRepository.existsByEmail(client.getEmail())) {
            throw new RuntimeException("Client with email already exists: " + client.getEmail());
        }
        return clientRepository.save(client);
    }
    
    @Transactional
    public Client updateClient(Long id, Client clientDetails) {
        Client client = getClientById(id);
        
        // Check if email is being changed and if it already exists
        if (!client.getEmail().equals(clientDetails.getEmail()) 
            && clientRepository.existsByEmail(clientDetails.getEmail())) {
            throw new RuntimeException("Client with email already exists: " + clientDetails.getEmail());
        }
        
        client.setName(clientDetails.getName());
        client.setEmail(clientDetails.getEmail());
        client.setPhone(clientDetails.getPhone());
        client.setAddress(clientDetails.getAddress());
        
        return clientRepository.save(client);
    }
    
    @Transactional
    public void deleteClient(Long id) {
        Client client = getClientById(id);
        clientRepository.delete(client);
    }
}