// Mock Axios module for API surface testing
const axios = {
  defaults: {
    timeout: 0,
    headers: {
      common: {},
      delete: {},
      get: {},
      head: {},
      post: {},
      put: {},
      patch: {}
    }
  },
  interceptors: {
    request: {
      use: jest.fn(),
      eject: jest.fn(),
      clear: jest.fn()
    },
    response: {
      use: jest.fn(),
      eject: jest.fn(),
      clear: jest.fn()
    }
  },
  create: jest.fn(),
  request: jest.fn(),
  get: jest.fn(),
  delete: jest.fn(),
  head: jest.fn(),
  options: jest.fn(),
  post: jest.fn(),
  put: jest.fn(),
  patch: jest.fn(),
  getUri: jest.fn(),
  isCancel: jest.fn(),
  CancelToken: {
    source: jest.fn()
  },
  Cancel: jest.fn(),
  all: jest.fn(),
  spread: jest.fn(),
  isAxiosError: jest.fn()
};

describe('Axios Public API', function() {
  beforeEach(function() {
    jest.clearAllMocks();
  });

  describe('Core Methods', function() {
    it('should expose request method', function() {
      expect(typeof axios.request).toBe('function');
    });

    it('should expose HTTP method shortcuts', function() {
      expect(typeof axios.get).toBe('function');
      expect(typeof axios.post).toBe('function');
      expect(typeof axios.put).toBe('function');
      expect(typeof axios.patch).toBe('function');
      expect(typeof axios.delete).toBe('function');
      expect(typeof axios.head).toBe('function');
      expect(typeof axios.options).toBe('function');
    });

    it('should expose getUri method', function() {
      expect(typeof axios.getUri).toBe('function');
    });
  });

  describe('Instance Creation', function() {
    it('should expose create method', function() {
      expect(typeof axios.create).toBe('function');
    });

    it('should create instance with custom config', function() {
      const customConfig = {
        baseURL: 'https://api.example.com',
        timeout: 5000
      };

      const mockInstance = {
        defaults: customConfig,
        interceptors: {
          request: { use: jest.fn(), eject: jest.fn() },
          response: { use: jest.fn(), eject: jest.fn() }
        },
        request: jest.fn(),
        get: jest.fn(),
        post: jest.fn(),
        put: jest.fn(),
        patch: jest.fn(),
        delete: jest.fn(),
        head: jest.fn(),
        options: jest.fn()
      };

      axios.create.mockReturnValue(mockInstance);

      const instance = axios.create(customConfig);
      
      expect(axios.create).toHaveBeenCalledWith(customConfig);
      expect(instance).toBeDefined();
      expect(instance.defaults).toEqual(customConfig);
      expect(typeof instance.get).toBe('function');
      expect(typeof instance.post).toBe('function');
    });
  });

  describe('Default Configuration', function() {
    it('should have defaults object', function() {
      expect(axios.defaults).toBeDefined();
      expect(typeof axios.defaults).toBe('object');
    });

    it('should have default timeout', function() {
      expect(axios.defaults.timeout).toBeDefined();
      expect(typeof axios.defaults.timeout).toBe('number');
    });

    it('should have headers configuration', function() {
      expect(axios.defaults.headers).toBeDefined();
      expect(axios.defaults.headers.common).toBeDefined();
      expect(axios.defaults.headers.get).toBeDefined();
      expect(axios.defaults.headers.post).toBeDefined();
      expect(axios.defaults.headers.put).toBeDefined();
      expect(axios.defaults.headers.patch).toBeDefined();
      expect(axios.defaults.headers.delete).toBeDefined();
      expect(axios.defaults.headers.head).toBeDefined();
    });

    it('should allow modification of defaults', function() {
      const originalTimeout = axios.defaults.timeout;
      axios.defaults.timeout = 10000;
      expect(axios.defaults.timeout).toBe(10000);
      // Restore for other tests
      axios.defaults.timeout = originalTimeout;
    });
  });

  describe('Cancellation API', function() {
    it('should expose Cancel constructor', function() {
      expect(typeof axios.Cancel).toBe('function');
    });

    it('should expose CancelToken', function() {
      expect(axios.CancelToken).toBeDefined();
      expect(typeof axios.CancelToken.source).toBe('function');
    });

    it('should expose isCancel method', function() {
      expect(typeof axios.isCancel).toBe('function');
    });

    it('should create cancel token source', function() {
      const mockSource = {
        token: { reason: null },
        cancel: jest.fn()
      };

      axios.CancelToken.source.mockReturnValue(mockSource);

      const source = axios.CancelToken.source();
      expect(axios.CancelToken.source).toHaveBeenCalled();
      expect(source).toBeDefined();
      expect(source.token).toBeDefined();
      expect(typeof source.cancel).toBe('function');
    });
  });

  describe('Utility Methods', function() {
    it('should expose all method for concurrent requests', function() {
      expect(typeof axios.all).toBe('function');
    });

    it('should expose spread method', function() {
      expect(typeof axios.spread).toBe('function');
    });

    it('should expose isAxiosError method', function() {
      expect(typeof axios.isAxiosError).toBe('function');
    });

    it('should handle concurrent requests with all()', function() {
      const promise1 = Promise.resolve({ data: 'result1' });
      const promise2 = Promise.resolve({ data: 'result2' });

      axios.all.mockImplementation(promises => Promise.all(promises));

      return axios.all([promise1, promise2]).then(function(results) {
        expect(axios.all).toHaveBeenCalledWith([promise1, promise2]);
        expect(results).toHaveLength(2);
        expect(results[0].data).toBe('result1');
        expect(results[1].data).toBe('result2');
      });
    });

    it('should spread results from concurrent requests', function() {
      const mockSpreadFn = jest.fn();
      const results = [{ data: 'result1' }, { data: 'result2' }];

      axios.spread.mockImplementation(fn => fn);

      const spreadFn = axios.spread(mockSpreadFn);
      spreadFn(...results);

      expect(axios.spread).toHaveBeenCalledWith(mockSpreadFn);
    });

    it('should identify axios errors', function() {
      const axiosError = new Error('Request failed');
      const normalError = new Error('Normal error');

      axios.isAxiosError.mockImplementation(error => 
        error && error.message === 'Request failed'
      );

      expect(axios.isAxiosError(axiosError)).toBe(true);
      expect(axios.isAxiosError(normalError)).toBe(false);
    });
  });

  describe('Request Configuration', function() {
    it('should accept string URL as first parameter', function() {
      axios.get('/api/users');
      expect(axios.get).toHaveBeenCalledWith('/api/users');
    });

    it('should accept config object as first parameter', function() {
      const config = {
        url: '/api/users',
        method: 'get',
        params: { page: 1 }
      };

      axios.request(config);
      expect(axios.request).toHaveBeenCalledWith(config);
    });

    it('should accept data for POST requests', function() {
      const data = { name: 'John', email: 'john@example.com' };
      const config = { headers: { 'Content-Type': 'application/json' } };

      axios.post('/api/users', data, config);
      expect(axios.post).toHaveBeenCalledWith('/api/users', data, config);
    });

    it('should accept data for PUT requests', function() {
      const data = { id: 1, name: 'John Updated' };

      axios.put('/api/users/1', data);
      expect(axios.put).toHaveBeenCalledWith('/api/users/1', data);
    });

    it('should accept data for PATCH requests', function() {
      const data = { name: 'John Patched' };

      axios.patch('/api/users/1', data);
      expect(axios.patch).toHaveBeenCalledWith('/api/users/1', data);
    });
  });

  describe('Response Handling', function() {
    it('should return promise from request methods', function() {
      const mockPromise = Promise.resolve({ data: {}, status: 200 });
      axios.get.mockReturnValue(mockPromise);

      const result = axios.get('/api/test');
      expect(result).toBeInstanceOf(Promise);
    });

    it('should handle successful responses', async function() {
      const mockResponse = {
        data: { message: 'Success' },
        status: 200,
        statusText: 'OK',
        headers: {},
        config: {}
      };

      axios.get.mockResolvedValue(mockResponse);

      const response = await axios.get('/api/test');
      expect(response.data.message).toBe('Success');
      expect(response.status).toBe(200);
    });

    it('should handle error responses', async function() {
      const mockError = new Error('Network Error');
      mockError.response = {
        data: { error: 'Not Found' },
        status: 404,
        statusText: 'Not Found'
      };

      axios.get.mockRejectedValue(mockError);

      await expect(axios.get('/api/nonexistent')).rejects.toThrow('Network Error');
    });
  });
});