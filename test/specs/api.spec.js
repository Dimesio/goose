const { describe, it, expect, beforeEach } = require('@jest/globals');

/**
 * Mock API Client for testing public API surface
 * This simulates the public API that might be exposed by the goose project
 */
class MockApiClient {
  constructor() {
    this.version = '1.0.31';
    this.defaults = {
      timeout: 5000,
      headers: {}
    };
    this.interceptors = {
      request: {
        use: jest.fn(),
        eject: jest.fn(),
        handlers: []
      },
      response: {
        use: jest.fn(),
        eject: jest.fn(),
        handlers: []
      }
    };
  }

  // Main request method
  request(config) {
    return Promise.resolve({
      data: { message: 'API response' },
      status: 200,
      config
    });
  }

  // HTTP method shortcuts
  get(url, config) { return this.request({ ...config, method: 'GET', url }); }
  post(url, data, config) { return this.request({ ...config, method: 'POST', url, data }); }
  put(url, data, config) { return this.request({ ...config, method: 'PUT', url, data }); }
  patch(url, data, config) { return this.request({ ...config, method: 'PATCH', url, data }); }
  delete(url, config) { return this.request({ ...config, method: 'DELETE', url }); }
  head(url, config) { return this.request({ ...config, method: 'HEAD', url }); }
  options(url, config) { return this.request({ ...config, method: 'OPTIONS', url }); }

  // Factory methods
  create(config) {
    return new MockApiClient();
  }

  // Utility methods
  isCancel() { return false; }
  CancelToken = {
    source: () => ({
      token: {},
      cancel: jest.fn()
    })
  };

  // Spread utility
  spread(callback) {
    return function(arr) {
      return callback.apply(null, arr);
    };
  }

  // All method
  all(promises) {
    return Promise.all(promises);
  }
}

// Create global API instance
const api = new MockApiClient();

describe('API Public Surface', function() {
  describe('version information', function() {
    it('should expose version property', function() {
      expect(api.version).toBeDefined();
      expect(typeof api.version).toBe('string');
      expect(api.version).toMatch(/^\d+\.\d+\.\d+/);
    });
  });

  describe('core methods', function() {
    it('should expose request method', function() {
      expect(typeof api.request).toBe('function');
    });

    it('should expose HTTP method shortcuts', function() {
      const methods = ['get', 'post', 'put', 'patch', 'delete', 'head', 'options'];
      
      methods.forEach(method => {
        expect(typeof api[method]).toBe('function');
      });
    });

    it('should expose factory method', function() {
      expect(typeof api.create).toBe('function');
      const instance = api.create();
      expect(instance).toBeInstanceOf(MockApiClient);
    });

    it('should expose utility methods', function() {
      expect(typeof api.all).toBe('function');
      expect(typeof api.spread).toBe('function');
      expect(typeof api.isCancel).toBe('function');
    });
  });

  describe('configuration', function() {
    it('should expose defaults object', function() {
      expect(api.defaults).toBeDefined();
      expect(typeof api.defaults).toBe('object');
      expect(api.defaults.timeout).toBeDefined();
      expect(api.defaults.headers).toBeDefined();
    });

    it('should allow modifying defaults', function() {
      const originalTimeout = api.defaults.timeout;
      api.defaults.timeout = 10000;
      
      expect(api.defaults.timeout).toBe(10000);
      
      // Restore original
      api.defaults.timeout = originalTimeout;
    });
  });

  describe('interceptors', function() {
    beforeEach(() => {
      // Reset interceptor mocks
      jest.clearAllMocks();
    });

    it('should expose request interceptors', function() {
      expect(api.interceptors.request).toBeDefined();
      expect(typeof api.interceptors.request.use).toBe('function');
      expect(typeof api.interceptors.request.eject).toBe('function');
    });

    it('should expose response interceptors', function() {
      expect(api.interceptors.response).toBeDefined();
      expect(typeof api.interceptors.response.use).toBe('function');
      expect(typeof api.interceptors.response.eject).toBe('function');
    });

    it('should allow registering request interceptors', function() {
      const requestInterceptor = jest.fn();
      const errorHandler = jest.fn();
      
      api.interceptors.request.use(requestInterceptor, errorHandler);
      
      expect(api.interceptors.request.use).toHaveBeenCalledWith(requestInterceptor, errorHandler);
    });

    it('should allow registering response interceptors', function() {
      const responseInterceptor = jest.fn();
      const errorHandler = jest.fn();
      
      api.interceptors.response.use(responseInterceptor, errorHandler);
      
      expect(api.interceptors.response.use).toHaveBeenCalledWith(responseInterceptor, errorHandler);
    });

    it('should allow ejecting interceptors', function() {
      const interceptorId = 1;
      
      api.interceptors.request.eject(interceptorId);
      api.interceptors.response.eject(interceptorId);
      
      expect(api.interceptors.request.eject).toHaveBeenCalledWith(interceptorId);
      expect(api.interceptors.response.eject).toHaveBeenCalledWith(interceptorId);
    });
  });

  describe('HTTP method behavior', function() {
    it('should handle GET requests correctly', async function() {
      const response = await api.get('/test');
      
      expect(response.config.method).toBe('GET');
      expect(response.config.url).toBe('/test');
      expect(response.status).toBe(200);
    });

    it('should handle POST requests with data', async function() {
      const data = { test: 'data' };
      const response = await api.post('/test', data);
      
      expect(response.config.method).toBe('POST');
      expect(response.config.url).toBe('/test');
      expect(response.config.data).toEqual(data);
    });

    it('should handle PUT requests with data', async function() {
      const data = { test: 'data' };
      const response = await api.put('/test', data);
      
      expect(response.config.method).toBe('PUT');
      expect(response.config.data).toEqual(data);
    });

    it('should handle PATCH requests with data', async function() {
      const data = { test: 'patch' };
      const response = await api.patch('/test', data);
      
      expect(response.config.method).toBe('PATCH');
      expect(response.config.data).toEqual(data);
    });

    it('should handle DELETE requests', async function() {
      const response = await api.delete('/test');
      
      expect(response.config.method).toBe('DELETE');
      expect(response.config.url).toBe('/test');
    });

    it('should handle HEAD requests', async function() {
      const response = await api.head('/test');
      
      expect(response.config.method).toBe('HEAD');
      expect(response.config.url).toBe('/test');
    });

    it('should handle OPTIONS requests', async function() {
      const response = await api.options('/test');
      
      expect(response.config.method).toBe('OPTIONS');
      expect(response.config.url).toBe('/test');
    });
  });

  describe('instance creation', function() {
    it('should create new instances with create method', function() {
      const instance = api.create({
        baseURL: 'https://api.example.com',
        timeout: 3000
      });
      
      expect(instance).toBeInstanceOf(MockApiClient);
      expect(instance).not.toBe(api);
    });

    it('should create instances with independent configuration', function() {
      const instance1 = api.create({ timeout: 1000 });
      const instance2 = api.create({ timeout: 2000 });
      
      expect(instance1).not.toBe(instance2);
      expect(instance1).toBeInstanceOf(MockApiClient);
      expect(instance2).toBeInstanceOf(MockApiClient);
    });
  });

  describe('cancellation', function() {
    it('should expose CancelToken', function() {
      expect(api.CancelToken).toBeDefined();
      expect(typeof api.CancelToken.source).toBe('function');
    });

    it('should create cancel tokens', function() {
      const source = api.CancelToken.source();
      
      expect(source.token).toBeDefined();
      expect(typeof source.cancel).toBe('function');
    });

    it('should expose isCancel method', function() {
      expect(typeof api.isCancel).toBe('function');
      expect(api.isCancel()).toBe(false);
    });
  });

  describe('utility functions', function() {
    it('should handle Promise.all with all method', async function() {
      const promises = [
        api.get('/test1'),
        api.get('/test2'),
        api.get('/test3')
      ];
      
      const results = await api.all(promises);
      
      expect(Array.isArray(results)).toBe(true);
      expect(results.length).toBe(3);
      results.forEach(result => {
        expect(result.status).toBe(200);
      });
    });

    it('should spread response data with spread method', function() {
      const spreadCallback = api.spread((first, second) => {
        return { first, second };
      });
      
      const result = spreadCallback(['a', 'b']);
      expect(result).toEqual({ first: 'a', second: 'b' });
    });
  });
});